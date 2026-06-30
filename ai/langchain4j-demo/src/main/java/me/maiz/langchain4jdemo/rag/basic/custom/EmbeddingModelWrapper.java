package me.maiz.langchain4jdemo.rag.basic.custom;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.listener.EmbeddingModelListener;
import dev.langchain4j.model.output.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
@Slf4j
public class EmbeddingModelWrapper implements EmbeddingModel {
    private final EmbeddingModel delegate;
    private final int batchSize;

    public EmbeddingModelWrapper(EmbeddingModel delegate, int batchSize) {
        log.debug("使用嵌入包装器，控制批量大小: {}", batchSize);
        this.delegate = delegate;
        this.batchSize = batchSize;
    }
    @Override
    public Response<Embedding> embed(String text) {
        return delegate.embed(text);
    }

    @Override
    public Response<Embedding> embed(TextSegment textSegment) {
        return delegate.embed(textSegment);
    }

    @Override
    public Response<List<Embedding>> embedAll(List<TextSegment> textSegments) {
        log.debug("Starting to embed {} documents", textSegments.size());
        List<Embedding> allEmbeddings = new ArrayList<>();
        int total = textSegments.size();

        for (int i = 0; i < total; i += batchSize) {
            int end = Math.min(i + batchSize, total);
            List<TextSegment> batch = textSegments.subList(i, end);

            log.debug("嵌入批次 {}-{}，共 {} 个文本段", i, end, batch.size());
            Response<List<Embedding>> batchResponse = delegate.embedAll(batch);
            allEmbeddings.addAll(batchResponse.content());

            // 可选：添加延迟避免速率限制
            if (end < total) {
                try {
                    Thread.sleep(100); // 100ms 延迟
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        return Response.from(allEmbeddings);
    }

    @Override
    public int dimension() {
        return delegate.dimension();
    }

    @Override
    public String modelName() {
        return delegate.modelName();
    }

    @Override
    public EmbeddingModel addListener(EmbeddingModelListener listener) {
        return delegate.addListener(listener);
    }

    @Override
    public EmbeddingModel addListeners(List<EmbeddingModelListener> listeners) {
        return delegate.addListeners(listeners);
    }
}
