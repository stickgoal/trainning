package com.example.mcp.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * 解析 classpath 中的 MCP Server 脚本到文件系统绝对路径。
 *
 * <p>在开发环境（spring-boot:run）下，classpath 资源位于 target/classes；
 * 打包后位于 jar 内部（非文件）。本解析器优先使用 classpath 资源对应的真实文件，
 * 失败时在 src/main/resources 回退，确保本地运行与开发调试都能找到脚本。
 */
public final class McpResourceResolver {

    private static final Logger log = LoggerFactory.getLogger(McpResourceResolver.class);

    private McpResourceResolver() {
    }

    /**
     * 将 classpath 资源路径（如 mcp-server/weather-mcp-server.js）解析为绝对文件路径。
     *
     * @param resourcePath classpath 相对路径
     * @return 文件系统的绝对路径
     * @throws IllegalStateException 当脚本在任何已知位置都找不到时
     */
    public static String resolve(String resourcePath) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader() != null
                ? Thread.currentThread().getContextClassLoader()
                : McpResourceResolver.class.getClassLoader();

        var resource = classLoader.getResource(resourcePath);
        if (resource != null) {
            try {
                File file = new File(resource.toURI());
                if (file.exists()) {
                    return file.getAbsolutePath();
                }
            } catch (Exception e) {
                log.debug("classpath 资源无法作为文件解析: {}", resourcePath, e);
            }
        }

        // 开发环境回退：从项目源码目录查找
        String devPath = "src/main/resources/" + resourcePath;
        File devFile = new File(devPath);
        if (devFile.exists()) {
            return devFile.getAbsolutePath();
        }

        throw new IllegalStateException("MCP server script not found: " + resourcePath
                + ". 请确认文件存在于 src/main/resources/" + resourcePath
                + "，并在该目录执行过 'npm install'。");
    }
}
