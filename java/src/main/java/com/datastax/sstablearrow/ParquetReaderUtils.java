package com.datastax.sstablearrow;

import java.util.function.Consumer;

import org.apache.arrow.dataset.file.FileFormat;
import org.apache.arrow.dataset.file.FileSystemDatasetFactory;
import org.apache.arrow.dataset.jni.NativeMemoryPool;
import org.apache.arrow.dataset.scanner.ScanOptions;
import org.apache.arrow.dataset.scanner.ScanTask;
import org.apache.arrow.dataset.scanner.Scanner;
import org.apache.arrow.dataset.source.Dataset;
import org.apache.arrow.dataset.source.DatasetFactory;
import org.apache.arrow.vector.VectorSchemaRoot;
import org.apache.arrow.vector.ipc.ArrowReader;

public class ParquetReaderUtils
{
    public static void read(String path, Consumer<VectorSchemaRoot> callback) throws Exception
    {
        try (DatasetFactory factory = new FileSystemDatasetFactory(ArrowUtils.ALLOCATOR, NativeMemoryPool.getDefault(), FileFormat.PARQUET, path);
             Dataset dataset = factory.finish();
             Scanner scanner = dataset.newScan(new ScanOptions(100)))
        {
            for (ScanTask t : scanner.scan())
            {
                try (ArrowReader reader = t.execute())
                {
                    while (reader.loadNextBatch())
                    {
                        callback.accept(reader.getVectorSchemaRoot());
                    }
                }
            }
        }
    }
}
