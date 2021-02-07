package com.bugu.things.annatation.compier;

import com.bugu.things.annatation.StorageType;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
public class StorageTypeCompiler extends AbstractProcessor {

    private Types typeUtils;
    private Elements elementUtils;
    private Filer filer;
    private Messager messager;
    private Map<String, FactoryGroupedClasses> factoryClasses = new LinkedHashMap<String, FactoryGroupedClasses>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        print("init");
        typeUtils = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
    }


    @Override
    public SourceVersion getSupportedSourceVersion() {
        SourceVersion latest = SourceVersion.latest();
        print("getSupportedSourceVersion : " + latest);
        return latest;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        print("getSupportedAnnotationTypes : ");
        LinkedHashSet<String> TYPES = new LinkedHashSet<>();
        TYPES.add(StorageType.class.getCanonicalName());
        return TYPES;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnv) {
        print("process : " );
        try {

            // Scan classes
            for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(StorageType.class)) {
                print("process : ============================1===========================" );
                // Check if a class has been annotated with @Factory
                if (annotatedElement.getKind() != ElementKind.CLASS) {
                    throw new ProcessingException(annotatedElement, "Only classes can be annotated with @%s",
                            StorageType.class.getSimpleName());
                }
                print("process : ============================2===========================" );
                // We can cast it, because we know that it of ElementKind.CLASS
                TypeElement typeElement = (TypeElement) annotatedElement;
                print("process : ============================3===========================" );
                FactoryAnnotatedClass annotatedClass = new FactoryAnnotatedClass(typeElement);
                print("process : ============================4===========================" );
                checkValidClass(annotatedClass);
                print("process : ============================5===========================" );
                // Everything is fine, so try to add
                FactoryGroupedClasses factoryClass =
                        factoryClasses.get(annotatedClass.getQualifiedFactoryGroupName());
                print("process : ============================6===========================" );
                if (factoryClass == null) {
                    print("process : ============================7===========================" );
                    String qualifiedGroupName = annotatedClass.getQualifiedFactoryGroupName();
                    factoryClass = new FactoryGroupedClasses(qualifiedGroupName);
                    factoryClasses.put(qualifiedGroupName, factoryClass);
                }
                print("process : ============================8===========================" );
                print("process : id = " + annotatedClass.getId() );
                print("process : path = " + annotatedClass.getPath() );
                print("process : QualifiedFactoryGroupName = " + annotatedClass.getQualifiedFactoryGroupName() );
                print("process : SimpleFactoryGroupName = " + annotatedClass.getSimpleFactoryGroupName() );
                print("process : cap = " + annotatedClass.getCap() );
                print("process : max = " + annotatedClass.getMax() );
                print("process : mode = " + annotatedClass.getMode() );
                // Checks if id is conflicting with another @Factory annotated class with the same id
                factoryClass.add(annotatedClass);
            }

            print("process : ============================9===========================" );
            // Generate code
            for (FactoryGroupedClasses factoryClass : factoryClasses.values()) {
                print("process : ============================10===========================" );
                factoryClass.generateCode(processingEnv,elementUtils, filer);
            }
            print("process : ============================11===========================" );
            factoryClasses.clear();
        } catch (ProcessingException e) {
            print("process : ============================12===========================" );
            error(e.getElement(), e.getMessage());
        } catch (IOException e) {
            print("process : ============================13===========================" );
            error(null, e.getMessage());
        }
        print("process : ============================14===========================" );
        return true;
    }

    private void printElement(Element element) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("class = ").append(element.getClass()).append("\n")
                .append("kind = ").append(element.getKind()).append("\n")
                .append("simpleName = ").append(element.getSimpleName()).append("\n")
                .append("canonicalName = ").append(element.getClass().getCanonicalName()).append("\n")
                .append("name = ").append(element.getClass().getName()).append("\n")
                .append("element.getKind().getDeclaringClass() = ").append(element.getKind().getDeclaringClass()).append("\n")
                .append("element.getKind().getClass() = ").append(element.getKind().getClass()).append("\n")
                .append("element.getKind().name() = ").append(element.getKind().name()).append("\n")
        ;
        print(stringBuffer.toString());
    }


    /**
     * Checks if the annotated element observes our rules
     */
    private void checkValidClass(FactoryAnnotatedClass item) throws ProcessingException {

        // Cast to TypeElement, has more type specific methods
        TypeElement classElement = item.getTypeElement();

        if (!classElement.getModifiers().contains(Modifier.PUBLIC)) {
            throw new ProcessingException(classElement, "The class %s is not public.",
                    classElement.getQualifiedName().toString());
        }

        // Check if it's an abstract class
        if (classElement.getModifiers().contains(Modifier.ABSTRACT)) {
            throw new ProcessingException(classElement,
                    "The class %s is abstract. You can't annotate abstract classes with @%",
                    classElement.getQualifiedName().toString(), StorageType.class.getSimpleName());
        }

        // Check inheritance: Class must be childclass as specified in @Factory.type();
        TypeElement superClassElement =
                elementUtils.getTypeElement(item.getQualifiedFactoryGroupName());
        if (superClassElement.getKind() == ElementKind.INTERFACE) {
            // Check interface implemented
            if (!classElement.getInterfaces().contains(superClassElement.asType())) {
                throw new ProcessingException(classElement,
                        "The class %s annotated with @%s must implement the interface %s",
                        classElement.getQualifiedName().toString(), StorageType.class.getSimpleName(),
                        item.getQualifiedFactoryGroupName());
            }
        } else {
            // Check subclassing
            TypeElement currentClass = classElement;
            while (true) {
                TypeMirror superClassType = currentClass.getSuperclass();

                if (superClassType.getKind() == TypeKind.NONE) {
                    // Basis class (java.lang.Object) reached, so exit
                    throw new ProcessingException(classElement,
                            "The class %s annotated with @%s must inherit from %s",
                            classElement.getQualifiedName().toString(), StorageType.class.getSimpleName(),
                            item.getQualifiedFactoryGroupName());
                }

                if (superClassType.toString().equals(item.getQualifiedFactoryGroupName())) {
                    // Required super class found
                    break;
                }

                // Moving up in inheritance tree
                currentClass = (TypeElement) typeUtils.asElement(superClassType);
            }
        }

        // Check if an empty public constructor is given
        for (Element enclosed : classElement.getEnclosedElements()) {
            if (enclosed.getKind() == ElementKind.CONSTRUCTOR) {
                ExecutableElement constructorElement = (ExecutableElement) enclosed;
                if (constructorElement.getParameters().size() == 0 && constructorElement.getModifiers()
                        .contains(Modifier.PUBLIC)) {
                    // Found an empty constructor
                    return;
                }
            }
        }

        // No empty constructor found
        throw new ProcessingException(classElement,
                "The class %s must provide an public empty default constructor",
                classElement.getQualifiedName().toString());
    }


    private void print(String msg) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, String.format(MSG, msg));
    }

    public static final String MSG = "\n **** StorageTypeProcessor ->  %s **** \n";

    public void error(Element e, String msg) {
        messager.printMessage(Diagnostic.Kind.ERROR, msg, e);
    }
}