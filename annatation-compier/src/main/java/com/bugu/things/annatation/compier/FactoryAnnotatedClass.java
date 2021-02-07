package com.bugu.things.annatation.compier;

import com.bugu.things.annatation.StorageType;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;

/**
 * Author by xpl, Date on 2021/2/6.
 */
public class FactoryAnnotatedClass {

    private TypeElement annotatedClassElement;
    private String qualifiedGroupClassName;
    private String simpleFactoryGroupName;
    private String id;
    private String path;
    private int mode;
    private long cap;
    private long max;

    /**
     * @throws ProcessingException if id() from annotation is null
     */
    public FactoryAnnotatedClass(TypeElement classElement) throws ProcessingException {
        this.annotatedClassElement = classElement;
        StorageType annotation = classElement.getAnnotation(StorageType.class);
        id = annotation.id();
        path = annotation.path();
        mode = annotation.mode();
        cap = annotation.cap();
        max = annotation.max();

        if (id.isEmpty()) {
            throw new ProcessingException(classElement,
                    "id() in @%s for class %s is null or empty! that's not allowed",
                    StorageType.class.getSimpleName(), classElement.getQualifiedName().toString());
        }

        // Get the full QualifiedTypeName
        try {
            Class<?> clazz = annotation.type();
            qualifiedGroupClassName = clazz.getCanonicalName();
            simpleFactoryGroupName = clazz.getSimpleName();
        } catch (MirroredTypeException mte) {
            DeclaredType classTypeMirror = (DeclaredType) mte.getTypeMirror();
            TypeElement classTypeElement = (TypeElement) classTypeMirror.asElement();
            qualifiedGroupClassName = classTypeElement.getQualifiedName().toString();
            simpleFactoryGroupName = classTypeElement.getSimpleName().toString();
        }
    }

    /**
     * Get the id as specified in {@link StorageType#id()}.
     * return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Get the full qualified name of the type specified in  {@link StorageType#type()}.
     *
     * @return qualified name
     */
    public String getQualifiedFactoryGroupName() {
        return qualifiedGroupClassName;
    }

    /**
     * Get the simple name of the type specified in  {@link StorageType#type()}.
     *
     * @return qualified name
     */
    public String getSimpleFactoryGroupName() {
        return simpleFactoryGroupName;
    }

    /**
     * The original element that was annotated with @Factory
     */
    public TypeElement getTypeElement() {
        return annotatedClassElement;
    }

    public String getPath() {
        return path;
    }

    public int getMode() {
        return mode;
    }

    public long getCap() {
        return cap;
    }

    public long getMax() {
        return max;
    }
}
