package framework.core;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public class ClassDiscovery {
    private final String basePath;

    public ClassDiscovery(String basePath) {
        this.basePath = basePath.endsWith(File.separator) ? basePath : basePath + File.separator;
    }

    public List<Class<?>> discoverClasses(Class<? extends Annotation> annotation) {
        List<Class<?>> annotatedClasses = new ArrayList<>();
        File baseDir = new File(basePath);

        for (File file : getAllClassFiles(baseDir)) {
            String className = getClassNameFromFile(file);
            try {
                Class<?> clazz = Class.forName(className);
                if (clazz.isAnnotationPresent(annotation)) {
                    annotatedClasses.add(clazz);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return annotatedClasses;
    }

    private List<File> getAllClassFiles(File dir) {
        List<File> classFiles = new ArrayList<>();
        if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                if (file.isDirectory()) {
                    classFiles.addAll(getAllClassFiles(file));
                } else if (file.getName().endsWith(".class")) {
                    classFiles.add(file);
                }
            }
        }
        return classFiles;
    }

    private String getClassNameFromFile(File file) {
        String relativePath = file.getPath().replace(basePath, "").replace(".class", "");

        relativePath = relativePath.substring(relativePath.indexOf("classes") + 8);
        return relativePath.replace(File.separator, ".");
    }
}
