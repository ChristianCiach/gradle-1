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

package org.gradle.build.integtests

/**
* @author Hans Dockter
*/
class JavaProject {
    static final String JAVA_PROJECT_NAME = 'javaproject'
    static final String SHARED_NAME = 'shared'
    static final String API_NAME = 'api'
    static final String WEBAPP_1_NAME = 'webapp1'
    static final String SERVICES_NAME = 'services'

    static void execute(String gradleHome, String samplesDirName) {
        List projects = [SHARED_NAME, API_NAME, WEBAPP_1_NAME, SERVICES_NAME].collect {"JAVA_PROJECT_NAME/$it"} + JAVA_PROJECT_NAME
        String packagePrefix = 'build/classes/org/gradle'
        String testPackagePrefix = 'build/test-classes/org/gradle'

        File javaprojectDir = new File(samplesDirName, 'javaproject')
        Executer.execute(gradleHome, javaprojectDir.absolutePath, ['clean', 'test'], [], '', Executer.DEBUG)
        checkExistence(javaprojectDir, SHARED_NAME, packagePrefix, SHARED_NAME, 'Person.class')
        checkExistence(javaprojectDir, SHARED_NAME, packagePrefix, SHARED_NAME, 'main.properties')
        checkExistence(javaprojectDir, SHARED_NAME, testPackagePrefix, SHARED_NAME, 'PersonTest.class')
        checkExistence(javaprojectDir, SHARED_NAME, testPackagePrefix, SHARED_NAME, 'test.properties')
        checkExistence(javaprojectDir, API_NAME, packagePrefix, API_NAME, 'PersonList.class')
        checkExistence(javaprojectDir, "$SERVICES_NAME/$WEBAPP_1_NAME" as String, packagePrefix, WEBAPP_1_NAME, 'TestTest.class')

        Executer.execute(gradleHome, new File(javaprojectDir, "$SERVICES_NAME/$WEBAPP_1_NAME").absolutePath,
                ['clean', 'test'], [], '', Executer.DEBUG)
        checkExistence(javaprojectDir, SHARED_NAME, packagePrefix, SHARED_NAME, 'Person.class')
        checkExistence(javaprojectDir, SHARED_NAME, packagePrefix, SHARED_NAME, 'main.properties')
        checkExistence(javaprojectDir, SHARED_NAME, testPackagePrefix, SHARED_NAME, 'PersonTest.class')
        checkExistence(javaprojectDir, SHARED_NAME, testPackagePrefix, SHARED_NAME, 'test.properties')
        checkExistence(javaprojectDir, "$SERVICES_NAME/$WEBAPP_1_NAME" as String, packagePrefix, WEBAPP_1_NAME, 'TestTest.class')

        // This test is also important for test cleanup
        Executer.execute(gradleHome, javaprojectDir.absolutePath, ['clean'], [], '', Executer.DEBUG)
        projects.each {assert !(new File(samplesDirName, "$it/build").exists())}

    }

    static void checkExistence(File baseDir, String[] path) {
        File file = new File(baseDir, path.join('/'))
        try {
            assert file.exists()
        } catch (AssertionError e) {
            println("File: $file should exists, but does not!")
            throw e
        }
    }

    
}
