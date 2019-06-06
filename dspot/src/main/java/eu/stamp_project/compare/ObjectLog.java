package eu.stamp_project.compare;


import eu.stamp_project.testrunner.EntryPoint;
import org.apache.commons.io.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import static java.nio.charset.Charset.forName;

/**
 * User: Simon
 * Date: 23/10/15
 * Time: 14:31
 */
public class ObjectLog {

    private static ObjectLog singleton;

    private Map<String, Observation> observations;
    private MethodsHandler methodsHandler;
    private int maxDeep = 3;

    private ObjectLog() {
        this.observations = new LinkedHashMap<>();
        this.methodsHandler = new MethodsHandler();
    }

    private static ObjectLog getSingleton() {
        if (singleton == null) {
            singleton = new ObjectLog();
        }
        return singleton;
    }

    public static void reset() {
        singleton = new ObjectLog();
    }

    public static void log(Object objectToObserve, String objectObservedAsString, String id) {
        try {
            FileUtils.writeStringToFile(new File("/home/andrew/Skrivbord/log.txt"), "log", forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*if (objectToObserve == null) {
            getSingleton().addObservation(id, "null", null);
            return;
        }*/
        // todo debug
        System.out.println("zzzzzzzzzzzzzzz logging");
        System.out.println("objectObservedAsString");
        System.out.println(objectObservedAsString);
        getSingleton()._log(
                objectToObserve,
                objectToObserve,
                null,
                objectObservedAsString,
                id,
                0,
                new ArrayList<>()
        );
    }

    private void _log(Object startingObject,
                      Object objectToObserve,
                      Class<?> currentObservedClass,
                      String observedObjectAsString,
                      String id,
                      int deep,
                      List<Method> methodsToReachCurrentObject) {
        System.out.println("in _log, observedObjectAsString: " + observedObjectAsString);
        if (deep <= maxDeep) {
            final boolean primitive = Utils.isPrimitive(objectToObserve);
            final boolean primitiveArray = Utils.isPrimitiveArray(objectToObserve);
            final boolean primitiveCollectionOrMap = Utils.isPrimitiveCollectionOrMap(objectToObserve);
            if (objectToObserve == null) {
                System.out.println("in null");
                addObservation(id, observedObjectAsString, null);
            } else if (isSerializable(objectToObserve) &&
                    (primitive || primitiveArray || primitiveCollectionOrMap)) {
                System.out.println("in isSerializable");
                addObservation(id, observedObjectAsString, objectToObserve);
            } else if (Utils.isCollection(objectToObserve)) {
                System.out.println("in collection");
                addObservation(id, observedObjectAsString + ".isEmpty()", ((Collection) objectToObserve).isEmpty());
            } else if (Utils.isMap(objectToObserve)) {
                System.out.println("in collection");
                addObservation(id, observedObjectAsString + ".isEmpty()", ((Map) objectToObserve).isEmpty());
            } else if (!objectToObserve.getClass().getName().toLowerCase().contains("mock")) {
                System.out.println("in observeNotNullObject");
                if(objectToObserve.getClass().isArray())
                {
                    System.out.println("in first array");
                    Class componentType = getArrayComponentType(startingObject);
                    ArrayList<Integer> al = new ArrayList<>();
                    int dimensions =  1 + startingObject.getClass().getName().lastIndexOf('[');
                    for(int i =0;i<dimensions;i++){
                        al.add(0);
                    }
                    goThroughArray(startingObject,componentType,observedObjectAsString,id,deep,methodsToReachCurrentObject,0,al);
                } else {
                    observeNotNullObject(
                            startingObject,
                            currentObservedClass == null ? objectToObserve.getClass() : currentObservedClass,
                            observedObjectAsString,
                            id,
                            deep,
                            methodsToReachCurrentObject);

                }


            }
        }
    }

    public static boolean isSerializable(Object candidate) {
        try {
            new ObjectOutputStream(new ByteArrayOutputStream()).writeObject(candidate);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private void addObservation(String id, String observedObjectAsString, Object actualValue) {
        if (isSerializable(actualValue)) {
            if (actualValue instanceof String &&
                    // we forbid absolute paths
                    // we allow relative paths
                    // but it can be error-prone
                    // watch out
                    new File((String)actualValue).isAbsolute()) {
                return;
            }
            if (!observations.containsKey(id)) {
                observations.put(id, new Observation());
            }
            System.out.println("observedObjectAsString in addObservation");
            System.out.println(observedObjectAsString);
            System.out.println("actualValue");
            System.out.println(actualValue);
            observations.get(id).add(observedObjectAsString, actualValue);
        }
    }

    private Object chainInvocationOfMethods(List<Method> methodsToInvoke, Object startingObject) throws FailToObserveException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        FutureTask task = null;
        Object currentObject = null;
        try {
            try {
                task = new FutureTask<>(() -> methodsToInvoke.get(0).invoke(startingObject));
                executor.execute(task);
                currentObject = task.get(1, TimeUnit.SECONDS);
            } catch (Exception e) {
                throw new FailToObserveException();
            }
            for (int i = 1; i < methodsToInvoke.size(); i++) {
                Method method = methodsToInvoke.get(i);
                try {
                    final Object finalCurrentObject = currentObject;
                    task = new FutureTask<>(() -> method.invoke(finalCurrentObject));
                    executor.execute(task);
                    currentObject = task.get(1, TimeUnit.SECONDS);
                } catch (Exception e) {
                    throw new FailToObserveException();
                }
            }
        } finally {
            if (task != null) {
                task.cancel(true);
            }
            executor.shutdown();
        }
        return currentObject;
    }

    private void observeNotNullObject(Object startingObject,
                                      Class<?> currentObservedClass,
                                      String stringObject,
                                      String id,
                                      int deep,
                                      List<Method> methodsToReachCurrentObject) {
        System.out.println("in observeNotNullObject first line");
        System.out.println("currentObservedClass: " + currentObservedClass);
        try {
            if(currentObservedClass.isArray())
            {

                System.out.println("an array!");
                //System.out.println(currentObservedClass.getComponentType());
                //System.out.println(currentObservedClass.getComponentType().isArray());
                Class componentType = getArrayComponentType(startingObject);
                System.out.println(componentType);
                ArrayList<Integer> al = new ArrayList<>();
                int dimensions =  1 + startingObject.getClass().getName().lastIndexOf('[');
                for(int i =0;i<dimensions;i++){
                    al.add(0);
                }
                goThroughArray(startingObject,componentType,stringObject,id,deep,methodsToReachCurrentObject,0,al);
            } else {
                for (Method method : methodsHandler.getAllMethods(currentObservedClass)) {
                    System.out.println("in observeNotNullObject for");
                    try {
                        final ArrayList<Method> tmpListOfMethodsToReachCurrentObject = new ArrayList<>(methodsToReachCurrentObject);
                        tmpListOfMethodsToReachCurrentObject.add(method);
                        final Object result = chainInvocationOfMethods(tmpListOfMethodsToReachCurrentObject, startingObject);
                        if (startingObject.getClass().isAnonymousClass()) {
                            System.out.println("in observeNotNullObject if");
                            _log(startingObject,
                                    result,
                                    method.getReturnType(),
                                    "(" + stringObject + ")." + method.getName() + "()",
                                    id,
                                    deep + 1,
                                    tmpListOfMethodsToReachCurrentObject
                            );
                        } else {
                            System.out.println("in observeNotNullObject else");
                            String nameOfVisibleClass = getVisibleClass(currentObservedClass);
                            _log(startingObject,
                                    result,
                                    method.getReturnType(),
                                    "(" + nameOfVisibleClass + stringObject + ")." + method.getName() + "()",
                                    id,
                                    deep + 1,
                                    tmpListOfMethodsToReachCurrentObject
                            );
                        }
                        tmpListOfMethodsToReachCurrentObject.remove(method);
                    } catch (FailToObserveException ignored) {
                        // ignored, we just do nothing...
                    }
                }}
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void observeNotNullArrayObject(Object startingObject, Class currentObservedClass, String stringObject, String id, int deep, List<Method> methodsToReachCurrentObject,
                                           ArrayList<Integer> al) {
        System.out.println("in observeNotNullObject first line");
        System.out.println("currentObservedClass: " + currentObservedClass);
        try {
            for (Method method : methodsHandler.getAllMethods(currentObservedClass)) {
                System.out.println("in observeNotNullObject for");
                try {
                    final ArrayList<Method> tmpListOfMethodsToReachCurrentObject = new ArrayList<>(methodsToReachCurrentObject);
                    tmpListOfMethodsToReachCurrentObject.add(method);
                    final Object result = chainInvocationOfMethods(tmpListOfMethodsToReachCurrentObject, startingObject);
                    if (startingObject.getClass().isAnonymousClass()) {
                        // todo is this relevant for reference type arrays?
                    } else {
                        System.out.println("in observeNotNullObject else");
                        String nameOfVisibleClass = getVisibleClass(currentObservedClass);
                        StringBuilder sb = new StringBuilder();
                        for(int i = 0; i<al.size();i++){
                            sb.append("[" + al.get(i) + "]");
                        }
                        _log(startingObject,
                                result,
                                method.getReturnType(),
                                "(" + nameOfVisibleClass + stringObject + ")" + sb.toString() + "." + method.getName() + "()" ,
                                id,
                                deep + 1,
                                tmpListOfMethodsToReachCurrentObject
                        );
                    }
                    tmpListOfMethodsToReachCurrentObject.remove(method);
                } catch (FailToObserveException ignored) {
                    // ignored, we just do nothing...
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void goThroughArray(Object obj, Class currentObservedClass, String stringObject, String id, int deep,
                                List<Method> methodsToReachCurrentObject,int depth,ArrayList<Integer> al) {
        int size = Array.getLength(obj);
        for (int i = 0; i < size; i++) {
            al.set(depth,i);
            Object value = Array.get(obj, i);
            if (value.getClass().isArray()) {
                goThroughArray(value,
                        currentObservedClass,
                        stringObject,
                        id,
                        deep,
                        methodsToReachCurrentObject,(depth+1),al);
            } else {
                observeNotNullArrayObject(value,
                        currentObservedClass,
                        stringObject,
                        id,
                        deep,
                        methodsToReachCurrentObject,al);
            }
        }
    }


    private static Class getArrayComponentType(Object obj) {
        //System.out.println("in getArrayComponentType");
        //System.out.println(obj.getClass());
        int size = Array.getLength(obj);
        for (int i = 0; i < size; i++) {
            Object value = Array.get(obj, i);
            if (value.getClass().isArray()) {
                Class clazz = getArrayComponentType(value);
                if(clazz != null)
                    return clazz;
            } else {
                return obj.getClass().getComponentType();
            }
        }
        return null;
    }

    private String getVisibleClass(Class<?> currentObservedClass) {
        if (currentObservedClass == null || currentObservedClass == Object.class) {
            return "";
        } else if (Modifier.isPrivate(currentObservedClass.getModifiers()) ||
                Modifier.isProtected(currentObservedClass.getModifiers())) {
            return getVisibleClass(currentObservedClass.getSuperclass());
        } else {
            return "(" + currentObservedClass.getCanonicalName() + ")";
        }
    }

    public static Map<String, Observation> getObservations() {
        if (getSingleton().observations.isEmpty()) {
            return load();
        } else {
            return getSingleton().observations;
        }
    }

    private static final String OBSERVATIONS_PATH_FILE_NAME = "target/dspot/observations.ser";

    public static void save() {
        getSingleton().observations.values().forEach(Observation::purify);
        StringBuilder sb = new StringBuilder();
        try (FileOutputStream fout = new FileOutputStream(OBSERVATIONS_PATH_FILE_NAME)) {
            try (ObjectOutputStream oos = new ObjectOutputStream(fout)) {
                for(String o : getSingleton().observations.keySet()) {
                    System.out.println(o);
                    sb.append("\n" + o);
                }
                for(Observation o : getSingleton().observations.values()) {
                    for(String s : o.getObservationValues().keySet()){
                        System.out.println(s);
                        sb.append("\n" + s);
                        System.out.println(o.getObservationValues().get(s));
                        sb.append("\n" + o.getObservationValues().get(s));
                    }
                    System.out.println(o);
                    sb.append("\n" + o);
                }
                FileUtils.writeStringToFile(new File("/home/andrew/Skrivbord/test.txt"), sb.toString(), forName("UTF-8"));
                oos.writeObject(getSingleton().observations);
                System.out.println(
                        String.format("File saved to the following path: %s",
                                new File(OBSERVATIONS_PATH_FILE_NAME).getAbsolutePath())
                );
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static Map<String, Observation> load() {
        try (FileInputStream fi = new FileInputStream(new File(
                (EntryPoint.workingDirectory != null ? // in case we modified the working directory
                        EntryPoint.workingDirectory.getAbsolutePath() + "/" : "") +
                        OBSERVATIONS_PATH_FILE_NAME))) {
            try (ObjectInputStream oi = new ObjectInputStream(fi)) {
                return (Map) oi.readObject();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
