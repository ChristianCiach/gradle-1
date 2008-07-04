/*
 * Copyright 2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradle.api;

import groovy.lang.Closure;
import groovy.util.AntBuilder;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import org.gradle.api.plugins.Convention;
import org.gradle.api.internal.project.BuildScriptProcessor;

/**
 * @author Hans Dockter
 */
public interface Project {
    public static final String DEFAULT_PROJECT_FILE = "build.gradle";

    public static final String PATH_SEPARATOR = ":";

    public static final String DEFAULT_BUILD_DIR_NAME = "build";

    public static final String GRADLE_PROPERTIES = "gradle.properties";

    public static final String SYSTEM_PROP_PREFIX = "systemProp";

    Project getRootProject();

    File getRootDir();

    void setRootDir(File rootDir);
    
    File getBuildDir();

    String getBuildFileName();

    Project getParent();

    String getName();

    Map getChildProjects();

    Set<Project> getDependsOnProjects();

    void setProperty(String name, Object value);

    /**
     * This method is used when scripts access the project via project.x
     */
    Project getProject();

    Set getAllprojects();

    Set getSubprojects();

    Project usePlugin(String pluginName, Map customValues);

    Project usePlugin(Class pluginClass, Map customValues);

    /**
     * Returns the task object which has the same name the name argument. If no such task exists, an exception is thrown.
     *
     * @param name the name of the task to be returned
     * @return a task with the same name as the name argument
     * @throws InvalidUserDataException If no task with the given name exists.
     */
    Task task(String name);

    /**
     * Returns the task object which has the same name the name argument. If no such task exists, an exception is thrown.
     * If such a task exists, before the task is returned, the given closure is passed to the configure method of the task
     * object.
     * @see org.gradle.api.Task
     * 
     * @param name the name of the task to be returned
     * @param configurationClosure
     * @return a task with the same name as the name argument
     * @throws InvalidUserDataException If no task with the given name exists.
     */
    Task task(String name, Closure configurationClosure);

    /**
     * Creates a task with the given name. The created task is of the type DefaultTask.
     *
     * @param name The name of the task to be created
     * @return The newly created task object
     * @throws InvalidUserDataException If a task with the given name already exsists.
     */
    Task createTask(String name);

    /**
     * Creates a task with the given name. The created task is of the type DefaultTask. Before the task is returned,
     * the given action closure is passed to the doLast method of the task.
     *
     * @param name The name of the task to be created
     * @param action The closure to be passed to the doLast method of the created task
     * @return The newly created task object
     * @throws InvalidUserDataException If a task with the given name already exsists.
     */
    Task createTask(String name, TaskAction action);

    /**
     * Creates a task with the given name. The task creation depends on the args map. 
     *
     * @param name The name of the task to be created
     * @return The newly created task object
     * @throws InvalidUserDataException If a task with the given name already exsists.
     */
    Task createTask(Map args, String name);

    Task createTask(Map args, String name, TaskAction action);

    String getArchivesBaseName();

    void setArchivesBaseName(String archivesBaseName);

    String getPath();

    void dependsOn(String path);

    void dependsOn(String path, boolean evaluateDependsOnProject);

    Project evaluationDependsOn(String path);

    Project childrenDependOnMe();

    Project dependsOnChildren();

    Project dependsOnChildren(boolean evaluateDependsOnProject);

    Project findProject(String path);

    Project project(String path);

    Project project(String path, Closure configureClosure);

    SortedMap getAllTasks(boolean recursive);

    Set<Task> getTasksByName(String name, boolean recursive);

    File getProjectDir();

    /**
     * @param path An object which toString method value is interpreted as a relative path to the project dir
     * @return
     */
    File file(Object path);

    /**
     * Returns a file which gets validated according to the validation type passed to the method. Possible validations
     * are: NONE, EXISTS, IS_FILE, IS_DIRECTORY
     * @param path An object which toString method value is interpreted as a relative path to the project dir 
     * @param validation
     * @return a File, which path is the absolute path of the project directory plus the relative path of the method argument
     */
    File file(Object path, PathValidation validation);

    String absolutePath(String path);

    AntBuilder getAnt();

    DependencyManager getDependencies();

    Convention getConvention();

    void setConvention(Convention convention);

    int depthCompare(Project otherProject);
    int getDepth();

    DagAction getConfigureByDag();

    Map<String, Task> getTasks();

    Map getPluginApplyRegistry();

    public Project evaluate();

    public BuildScriptProcessor getBuildScriptProcessor();

    public void subprojects(ProjectAction action);

    public void allprojects(ProjectAction action);

    public void applyActions(Set<Project> projects, ProjectAction action);
}
