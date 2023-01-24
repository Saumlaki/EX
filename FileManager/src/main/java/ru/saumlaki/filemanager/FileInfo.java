package ru.saumlaki.filemanager;

import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class FileInfo {
    public enum FileType {
        FILE("F"), DIRECTORY("D");

        private String name;

        FileType(String f) {
            name = f;
        }

        public String getName() {
            return name;
        }
    }
    @Getter
    @Setter
    private String fileName;
    @Getter
    @Setter
    private FileType fileType;
    @Getter
    @Setter
    private long size;
    @Getter
    @Setter
    private LocalDateTime lastModified;

    public FileInfo(Path path) {
        try {
            this.fileName = path.getFileName().toString();
            this.fileType = Files.isDirectory(path) ? FileType.DIRECTORY : FileType.FILE;
            this.size = fileType == FileType.DIRECTORY ? -1L : Files.size(path);
            this.lastModified = LocalDateTime.ofInstant(Files.getLastModifiedTime(path).toInstant(), ZoneOffset.ofHours(0));
        } catch (IOException e) {
            throw new RuntimeException("Не могу создать файл по пути");
        }
    }
}
