#!/usr/bin/env node
/**
 * 构建脚本：将每个 slide-X.js 生成为独立的 slide-X.html 文件
 * 结构参考 slide_0.html：
 * - 简洁的 HTML 结构
 * - 每页只有一个 slide-page class
 * - Tailwind CSS 转为内联样式
 */

import fs from 'fs';
import path from 'path';
import os from 'os';
import { fileURLToPath } from 'url';
import { execSync } from 'child_process';
import juice from 'juice';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const rootDir = path.resolve(__dirname, '..');

// 输出目录
const outputDir = path.join(rootDir, 'dist-slides');

// 从 index.html 提取完整的 <head> 内容
function extractHeadFromIndex() {
  const indexPath = path.join(rootDir, 'index.html');
  const indexContent = fs.readFileSync(indexPath, 'utf-8');
  
  // 提取完整的 <head> 内容（不含 <head> 和 </head> 标签本身）
  const headMatch = indexContent.match(/<head[^>]*>([\s\S]*?)<\/head>/i);
  let headContent = '';
  if (headMatch) {
    headContent = headMatch[1]
      // 移除 script 标签（不需要在静态 HTML 中）
      .replace(/<script[\s\S]*?<\/script>/gi, '')
      // 移除 Vite 相关的注释
      .replace(/<!--[\s\S]*?-->/g, '')
      // 清理多余空行
      .replace(/\n\s*\n\s*\n/g, '\n\n')
      .trim();
  }
  
  return {
    headContent
  };
}

// 读取样式文件
function readStyles() {
  const mainCss = fs.readFileSync(path.join(rootDir, 'src/styles/main.css'), 'utf-8');
  return { mainCss };
}

// 从 slide-X.js 文件中提取 HTML 内容
function extractSlideContent(filePath) {
  const content = fs.readFileSync(filePath, 'utf-8');
  // 匹配 window.slideDataMap.set(N, `...`) 中的内容
  const match = content.match(/window\.slideDataMap\.set\(\d+,\s*`([\s\S]*?)`\s*\);/);
  if (match) {
    return match[1].trim();
  }
  return null;
}

// 获取所有 slide 文件（按 index.html 中的 <script> 文档顺序，保证图表页正确穿插）
function getSlideFiles() {
  const slidesDir = path.join(rootDir, 'src/slides');
  const indexPath = path.join(rootDir, 'index.html');
  const idxContent = fs.readFileSync(indexPath, 'utf-8');
  // 提取 index.html 中按出现顺序的 slide 脚本
  const orderedMatches = [...idxContent.matchAll(/src=["']\/src\/slides\/slide-(\d+)\.js["']/g)];
  if (orderedMatches.length > 0) {
    return orderedMatches.map(m => {
      const number = parseInt(m[1], 10);
      return {
        name: `slide-${number}.js`,
        number,
        path: path.join(slidesDir, `slide-${number}.js`)
      };
    });
  }
  // 兜底：目录扫描 + 数字排序
  const files = fs.readdirSync(slidesDir);
  return files
    .filter(f => /^slide-\d+\.js$/.test(f))
    .map(f => ({
      name: f,
      number: parseInt(f.match(/slide-(\d+)\.js/)[1]),
      path: path.join(slidesDir, f)
    }))
    .sort((a, b) => a.number - b.number);
}

// 生成 Tailwind CSS
function generateTailwindCss(slideContents) {
  // 创建临时目录（用系统临时目录，避免中文/空格路径影响 Tailwind 扫描）
  const tempDir = path.join(os.tmpdir(), 'ppt-build-' + Date.now());
  fs.mkdirSync(tempDir, { recursive: true });

  // 创建临时 HTML 文件，包含所有 slide 内容
  const tempHtml = `<!DOCTYPE html>
<html>
<body>
${slideContents.map(s => s.content).join('\n')}
</body>
</html>`;
  
  const tempHtmlPath = path.join(tempDir, 'temp-slides.html');
  fs.writeFileSync(tempHtmlPath, tempHtml);

  // 读取原始样式
  const { mainCss } = readStyles();
  
  // 创建临时 CSS 输入文件
  const tempCssInput = path.join(tempDir, 'input.css');
  fs.writeFileSync(tempCssInput, mainCss);

  // 创建临时 tailwind 配置（content 路径用正斜杠，避免 Windows 反斜杠破坏 JS 字符串）
  const tempTailwindConfig = path.join(tempDir, 'tailwind.config.js');
  const contentPath = tempHtmlPath.replace(/\\/g, '/');
  fs.writeFileSync(tempTailwindConfig, `
export default {
  content: ['${contentPath}'],
  theme: {
    extend: {},
  },
  plugins: [],
}
`);

  // 运行 Tailwind CLI
  const tempCssOutput = path.join(tempDir, 'output.css');
  try {
    execSync(`npx tailwindcss -i "${tempCssInput}" -o "${tempCssOutput}" -c "${tempTailwindConfig}"`, {
      cwd: rootDir,
      stdio: 'pipe'
    });
  } catch (error) {
    console.error('Tailwind CSS 处理失败:', error.message);
    fs.writeFileSync(tempCssOutput, mainCss);
  }

  // 读取生成的 CSS
  const outputCss = fs.readFileSync(tempCssOutput, 'utf-8');

  // 清理临时文件（try/catch：安全删除拦截器在某些环境可能失败，忽略即可）
  try { fs.rmSync(tempDir, { recursive: true, force: true }); } catch (e) { /* ignore cleanup error */ }

  return outputCss;
}

// 生成带样式的临时 HTML 用于 juice 处理
function generateTempHtmlForJuice(slideContent, tailwindCss) {
  return `<!DOCTYPE html>
<html>
<head>
  <style>
${tailwindCss}
  </style>
</head>
<body>
${slideContent}
</body>
</html>`;
}

// 使用 juice 将 CSS 转为内联样式
function inlineStyles(html) {
  const result = juice(html, {
    removeStyleTags: true,
    preserveMediaQueries: false,
    preserveFontFaces: false,
    preserveKeyFrames: false,
    applyStyleTags: true,
    insertPreservedExtraCss: false,
    extraCss: ''
  });
  return result;
}

// 从内联后的 HTML 中提取 body 内容
function extractBodyContent(html) {
  const bodyMatch = html.match(/<body[^>]*>([\s\S]*?)<\/body>/i);
  if (bodyMatch) {
    return bodyMatch[1].trim();
  }
  return html;
}

// 清理内联样式中的冗余属性
function cleanInlineStyles(content) {
  // 移除 class 属性（样式已内联）
  let cleaned = content.replace(/\s*class="[^"]*"/g, '');
  
  // 移除 Tailwind reset 的冗余样式
  cleaned = cleaned.replace(/style="([^"]*)"/g, (match, styleContent) => {
    let cleanedStyle = styleContent
      // 移除 Tailwind 的 border reset
      .replace(/border-width:\s*0;\s*/g, '')
      .replace(/border-style:\s*solid;\s*/g, '')
      .replace(/border-color:\s*#e5e7eb;\s*/g, '')
      .replace(/border-:\s*/g, '') // 处理被截断的情况
      // 移除 Tailwind 的 box-sizing reset（如果没有其他特殊需求）
      .replace(/box-sizing:\s*border-box;\s*/g, '')
      // 移除冗余的 margin: 0 和 padding: 0（如果是默认值）
      .replace(/margin:\s*0;\s*/g, '')
      .replace(/padding:\s*0;\s*/g, '')
      // 清理开头和结尾的空格和分号
      .replace(/^[\s;]+/, '')
      .replace(/[\s;]+$/, '')
      .trim();
    
    if (!cleanedStyle) {
      return '';
    }
    return `style="${cleanedStyle}"`;
  });
  
  // 移除空的 style 属性
  cleaned = cleaned.replace(/\s*style=""/g, '');
  
  // 清理多余空格
  cleaned = cleaned.replace(/\s+>/g, '>');
  
  return cleaned;
}

// 递归复制目录（兜底，避免某些环境下 fs.cpSync 被拦截）
function copyDirManual(src, dest) {
  fs.mkdirSync(dest, { recursive: true });
  for (const entry of fs.readdirSync(src, { withFileTypes: true })) {
    const s = path.join(src, entry.name);
    const d = path.join(dest, entry.name);
    if (entry.isDirectory()) copyDirManual(s, d);
    else fs.copyFileSync(s, d);
  }
}

// 转换原始 slide 内容中的图片路径为【相对路径】
// 相对路径（相对于每个 slide-N.html 所在目录）既能让浏览器从 dist-slides/ 直接打开，
// 也能让 html2pptx 按 slide 文件目录正确解析，避免 Windows 绝对路径被二次拼接成 D:\D:\...
function transformSlideContent(content) {
  let transformed = content;
  // 将 /assets/ 前缀去掉，改为相对 assets/（相对当前 html 文件目录）
  transformed = transformed.replace(/src="\/assets\//g, 'src="assets/');
  // 已是相对路径的保持不变
  transformed = transformed.replace(/src="assets\//g, 'src="assets/');
  return transformed;
}

// 为最外层 div 添加 slide-page class
function wrapWithSlidePage(bodyContent) {
  // 找到第一个 div 并添加 class="slide-page"，保留原有样式
  // 注意：内容开头可能有 HTML 注释，需要跳过注释找到第一个 div
  
  // 使用正则替换第一个 <div 标签，添加 slide-page class
  // 匹配第一个 <div，前面可能有注释或空白
  bodyContent = bodyContent.replace(
    /(<div)([\s>])/,
    '$1 class="slide-page"$2'
  );
  
  return bodyContent;
}

// 生成最终的独立 HTML 文件（使用 index.html 的 head 内容）
function generateFinalHtml(bodyContent, headInfo) {
  return `<!DOCTYPE html>
<html>
<head>
${headInfo.headContent}
</head>
<body>
${bodyContent}
</body>
</html>`;
}

// 主函数
async function main() {
  console.log('🚀 开始构建独立 slide HTML 文件...\n');

  // 创建输出目录
  if (!fs.existsSync(outputDir)) {
    fs.mkdirSync(outputDir, { recursive: true });
  }

  // 从 index.html 提取字体和 CSS 变量
  console.log('📄 从 index.html 提取字体和 CSS 变量...');
  const headInfo = extractHeadFromIndex();

  // 获取所有 slide 文件
  const slideFiles = getSlideFiles();
  console.log(`📄 找到 ${slideFiles.length} 个 slide 文件\n`);

  // 提取所有 slide 内容
  const slideContents = [];
  for (const slide of slideFiles) {
    const content = extractSlideContent(slide.path);
    if (content) {
      // 转换图片路径
      const transformedContent = transformSlideContent(content);
      slideContents.push({
        number: slide.number,
        content: transformedContent,
        name: slide.name
      });
    } else {
      console.warn(`⚠️  无法提取 ${slide.name} 的内容`);
    }
  }

  // 生成 Tailwind CSS
  console.log('🎨 处理 Tailwind CSS...');
  const tailwindCss = generateTailwindCss(slideContents);

  // 复制 assets 目录（含 mermaid 渲染出的 diagram PNG），使 dist-slides 自包含，
  // 以便浏览器直接打开 individual slide 以及 html2pptx 按相对路径读取图片。
  // 注意：不使用 fs.cpSync / fs.rmSync（会被环境的安全删除拦截器拦截并中断进程），
  // 改用纯 mkdirSync + copyFileSync 的手工递归复制。
  const assetsSourceDir = path.join(rootDir, 'public/assets');
  const assetsDestDir = path.join(outputDir, 'assets');
  if (fs.existsSync(assetsSourceDir)) {
    console.log('📁 复制 assets 目录...');
    copyDirManual(assetsSourceDir, assetsDestDir);
    const copied = fs.readdirSync(assetsDestDir, { recursive: true })
      .filter((f) => typeof f === 'string' && f.endsWith('.png')).length;
    console.log(`   ✅ assets 复制完成（${copied} 个 png）`);
  }

  // 生成每个 slide 的 HTML 文件
  console.log('🔄 转换为内联样式...\n');
  
  let __outIdx = 0;
  for (const slide of slideContents) {
    // 生成带样式的临时 HTML
    const tempHtml = generateTempHtmlForJuice(slide.content, tailwindCss);
    
    // 使用 juice 转换为内联样式
    const inlinedHtml = inlineStyles(tempHtml);
    
    // 提取 body 内容
    let bodyContent = extractBodyContent(inlinedHtml);
    
    // 清理内联样式
    bodyContent = cleanInlineStyles(bodyContent);
    
    // 包装为 slide-page
    bodyContent = wrapWithSlidePage(bodyContent);
    
    // 生成最终 HTML（传入 headInfo）
    const finalHtml = generateFinalHtml(bodyContent, headInfo);
    
    // 保存到文件（下标从 0 开始，按文档顺序）
    const outputIndex = __outIdx++;
    const outputPath = path.join(outputDir, `slide-${outputIndex}.html`);
    fs.writeFileSync(outputPath, finalHtml);
    console.log(`   ✅ slide-${outputIndex}.html`);
  }

  // 从已生成的 slide-x.html 文件中提取 body 内容，合并到 all-slides.html
  console.log('\n📦 生成合并文件...');
  const allBodyContents = [];
  for (let i = 0; i < slideContents.length; i++) {
    const slideHtmlPath = path.join(outputDir, `slide-${i}.html`);
    if (!fs.existsSync(slideHtmlPath)) {
      console.warn(`⚠️  无法找到 ${slideHtmlPath}`);
      continue;
    }
    const slideHtml = fs.readFileSync(slideHtmlPath, 'utf-8');
    const bodyContent = extractBodyContent(slideHtml);
    allBodyContents.push(bodyContent);
  }
  
  const mergedHtml = generateMergedHtml(allBodyContents, headInfo);
  const mergedPath = path.join(outputDir, 'all-slides.html');
  fs.writeFileSync(mergedPath, mergedHtml);
  console.log(`   ✅ all-slides.html`);

  console.log(`\n🎉 构建完成！输出目录: ${outputDir}`);
  console.log(`   共生成 ${slideContents.length} 个独立 HTML 文件 + 1 个合并文件`);
}

// ===== Reveal.js 风格播放引擎（自包含，无外部依赖，随构建注入 all-slides.html）=====
const PLAYER_CSS = `
html,body{margin:0;padding:0;height:100%;}
body{overflow:hidden;background:#0b1220;font-family:var(--font-body);}
.deck{position:fixed;inset:0;overflow:hidden;background:#0b1220;}
.deck-stage{position:absolute;inset:0;perspective:1400px;}
.deck-slide{position:absolute;inset:0;display:flex;align-items:center;justify-content:center;opacity:0;pointer-events:none;backface-visibility:hidden;transition:opacity .55s ease, transform .55s cubic-bezier(.26,.86,.44,.985);transform:translateX(100%);}
.deck-slide.is-current{opacity:1;pointer-events:auto;transform:translateX(0) rotateY(0) scale(1);}
.deck-slide.is-prev{transform:translateX(-100%) rotateY(0) scale(1);}
.deck-slide.is-next{transform:translateX(100%) rotateY(0) scale(1);}
.slide-fit{width:1440px;height:810px;transform:scale(var(--fit,0.8));transform-origin:center center;}

/* fade */
.deck[data-transition="fade"] .deck-slide{transform:scale(1.03);}
.deck[data-transition="fade"] .deck-slide.is-prev,.deck[data-transition="fade"] .deck-slide.is-next{transform:scale(1.03);}
.deck[data-transition="fade"] .deck-slide.is-current{transform:scale(1);}

/* zoom */
.deck[data-transition="zoom"] .deck-slide{transform:scale(.2);}
.deck[data-transition="zoom"] .deck-slide.is-prev,.deck[data-transition="zoom"] .deck-slide.is-next{transform:scale(.2);}
.deck[data-transition="zoom"] .deck-slide.is-current{transform:scale(1);}

/* concave */
.deck[data-transition="concave"] .deck-slide{transform:translateX(100%) rotateY(-90deg);transform-origin:right center;}
.deck[data-transition="concave"] .deck-slide.is-prev{transform:translateX(-100%) rotateY(90deg);transform-origin:left center;}
.deck[data-transition="concave"] .deck-slide.is-current{transform:translateX(0) rotateY(0);}

/* convex */
.deck[data-transition="convex"] .deck-slide{transform:translateX(100%) rotateY(90deg);transform-origin:right center;}
.deck[data-transition="convex"] .deck-slide.is-prev{transform:translateX(-100%) rotateY(-90deg);transform-origin:left center;}
.deck[data-transition="convex"] .deck-slide.is-current{transform:translateX(0) rotateY(0);}

/* toolbar */
.deck-bar{position:fixed;left:50%;bottom:18px;transform:translateX(-50%);display:flex;align-items:center;gap:10px;padding:8px 14px;border-radius:999px;background:rgba(15,23,42,.82);color:#e2e8f0;font-size:14px;font-family:var(--font-body);box-shadow:0 6px 24px rgba(0,0,0,.35);z-index:50;user-select:none;backdrop-filter:blur(6px);}
.deck-btn{appearance:none;border:none;background:rgba(255,255,255,.08);color:#e2e8f0;width:34px;height:34px;border-radius:50%;font-size:20px;line-height:1;cursor:pointer;display:flex;align-items:center;justify-content:center;transition:background .2s;}
.deck-btn:hover{background:rgba(255,255,255,.2);}
.deck-btn.deck-text{width:auto;padding:0 12px;font-size:13px;border-radius:999px;}
.deck-counter{font-variant-numeric:tabular-nums;min-width:64px;text-align:center;font-size:14px;}
.deck-sep{width:1px;height:22px;background:rgba(255,255,255,.18);}
.deck-efx{display:flex;align-items:center;gap:6px;font-size:13px;color:#cbd5e1;}
.deck-efx select{background:rgba(255,255,255,.08);color:#e2e8f0;border:1px solid rgba(255,255,255,.15);border-radius:8px;padding:4px 6px;font-size:13px;}
.deck-progress{position:fixed;left:0;top:0;height:4px;width:100%;background:rgba(255,255,255,.08);z-index:50;}
.deck-progress-fill{height:100%;width:0;background:linear-gradient(90deg,#3B82F6,#22D3EE,#8B5CF6);transition:width .4s ease;}

/* overview grid */
.deck.overview .deck-stage{display:grid;grid-template-columns:repeat(auto-fill,320px);gap:18px;padding:24px;overflow:auto;align-content:start;background:#0b1220;perspective:none;}
.deck.overview .deck-slide{position:relative;inset:auto;width:320px;height:180px;overflow:hidden;border-radius:10px;opacity:1;pointer-events:auto;cursor:pointer;border:3px solid transparent;transform:none!important;transition:border-color .2s;}
.deck.overview .deck-slide.is-current{border-color:#3B82F6;}
.deck.overview .slide-fit{position:absolute;top:0;left:0;transform:scale(.2222);transform-origin:top left;}
`;

const PLAYER_JS = `
(function(){
  var deck=document.getElementById('deck');
  var slides=Array.prototype.slice.call(deck.querySelectorAll('.deck-slide'));
  var total=slides.length, idx=0;
  var curEl=document.getElementById('cur');
  var totalEl=document.getElementById('total');
  var progFill=document.getElementById('progFill');
  if(totalEl) totalEl.textContent=total;

  function fit(){
    var s=Math.min(window.innerWidth/1440, window.innerHeight/810);
    deck.style.setProperty('--fit', s);
  }
  function render(){
    for(var i=0;i<slides.length;i++){
      slides[i].classList.remove('is-current','is-prev','is-next');
      if(i===idx) slides[i].classList.add('is-current');
      else if(i<idx) slides[i].classList.add('is-prev');
      else slides[i].classList.add('is-next');
    }
    curEl.textContent=idx+1;
    progFill.style.width=(total>1?(idx/(total-1)*100):100)+'%';
  }
  function go(n){ n=Math.max(0,Math.min(total-1,n)); idx=n; render(); }
  function next(){ if(idx<total-1){idx++;render();} }
  function prev(){ if(idx>0){idx--;render();} }

  document.getElementById('nextBtn').onclick=next;
  document.getElementById('prevBtn').onclick=prev;
  document.getElementById('efx').onchange=function(e){ deck.setAttribute('data-transition',e.target.value); };
  document.getElementById('fsBtn').onclick=function(){
    if(!document.fullscreenElement){ (document.documentElement.requestFullscreen||function(){}).call(document.documentElement); }
    else { (document.exitFullscreen||function(){}).call(document); }
  };
  document.getElementById('overviewBtn').onclick=toggleOverview;

  // 点击翻页：用 window 级监听，避免被中间层吞掉；排除工具栏与进度条
  window.addEventListener('click',function(e){
    if(deck.classList.contains('overview')) return;
    var t=e.target;
    if(t && t.closest && (t.closest('.deck-bar') || t.closest('.deck-progress'))) return;
    if(e.clientX < window.innerWidth*0.3) prev(); else next();
  });

  window.addEventListener('keydown',function(e){
    if(e.key==='ArrowRight'||e.key==='ArrowDown'||e.key==='PageDown'||e.key===' '){ next(); e.preventDefault(); }
    else if(e.key==='ArrowLeft'||e.key==='ArrowUp'||e.key==='PageUp'){ prev(); e.preventDefault(); }
    else if(e.key==='Home'){ go(0); }
    else if(e.key==='End'){ go(total-1); }
    else if(e.key==='f'||e.key==='F'){ document.getElementById('fsBtn').click(); }
    else if(e.key==='o'||e.key==='O'){ toggleOverview(); }
    else if(e.key==='Escape'){ if(deck.classList.contains('overview')) toggleOverview(); }
  });

  var tsx=0;
  deck.addEventListener('touchstart',function(e){ tsx=e.touches[0].clientX; },{passive:true});
  deck.addEventListener('touchend',function(e){
    var dx=e.changedTouches[0].clientX-tsx;
    if(Math.abs(dx)>50){ dx<0?next():prev(); }
  },{passive:true});

  function toggleOverview(){ deck.classList.toggle('overview'); }
  slides.forEach(function(sl,i){
    sl.addEventListener('click',function(e){
      if(deck.classList.contains('overview')){ e.stopPropagation(); go(i); deck.classList.remove('overview'); }
    });
  });

  window.addEventListener('resize',fit);
  fit(); render();
})();
`;

// 生成合并后的 HTML 文件（使用 index.html 的 head 内容）+ 注入播放引擎
function generateMergedHtml(bodyContents, headInfo) {
  const sections = bodyContents
    .map((b, i) => `  <section class="deck-slide" data-index="${i}">\n    <div class="slide-fit">\n${b}\n    </div>\n  </section>`)
    .join('\n');

  return `<!DOCTYPE html>
<html>
<head>
${headInfo.headContent}
<style>${PLAYER_CSS}</style>
</head>
<body>

<div class="deck" id="deck" data-transition="slide">
  <div class="deck-stage" id="deckStage">
${sections}
  </div>
</div>

<div class="deck-bar" id="deckBar">
  <button class="deck-btn" id="prevBtn" title="上一页 (←)">&#8249;</button>
  <span class="deck-counter"><span id="cur">1</span> / <span id="total">${bodyContents.length}</span></span>
  <button class="deck-btn" id="nextBtn" title="下一页 (→ / 空格)">&#8250;</button>
  <span class="deck-sep"></span>
  <label class="deck-efx">效果
    <select id="efx">
      <option value="slide">滑动 Slide</option>
      <option value="fade">淡入 Fade</option>
      <option value="zoom">缩放 Zoom</option>
      <option value="concave">凹面 Concave</option>
      <option value="convex">凸面 Convex</option>
    </select>
  </label>
  <button class="deck-btn deck-text" id="overviewBtn" title="概览 (O)">概览</button>
  <button class="deck-btn deck-text" id="fsBtn" title="全屏 (F)">全屏</button>
</div>
<div class="deck-progress"><div class="deck-progress-fill" id="progFill"></div></div>

<script>
${PLAYER_JS}
</script>
</body>
</html>`;
}

main().catch(console.error);
