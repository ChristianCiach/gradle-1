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
class GroovyProject {
    static final String GROOVY_PROJECT_NAME = 'groovyproject'

    static void execute(String gradleHome, String samplesDirName) {
        String packagePrefix = 'build/classes/org/gradle'
        String testPackagePrefix = 'build/test-classes/org/gradle'

        List mainFiles = ['JavaPerson', 'GroovyPerson', 'GroovyJavaPerson']
        List testFiles = ['JavaPersonTest', 'GroovyPersonTest', 'GroovyJavaPersonTest']

        File groovyProjectDir = new File(samplesDirName, GROOVY_PROJECT_NAME)
        println(Executer.execute(gradleHome, groovyProjectDir.absolutePath, ['clean', 'libs'], '', Executer.DEBUG).output)
        mainFiles.each { JavaProject.checkExistence(groovyProjectDir, packagePrefix, it + ".class")}

        testFiles.each { JavaProject.checkExistence(groovyProjectDir, testPackagePrefix, it + ".class") }

        // The test produce marker files with the name of the test class
        testFiles.each { JavaProject.checkExistence(groovyProjectDir, 'build', it) }

        String unjarPath = "$groovyProjectDir/build/unjar"
        AntBuilder ant = new AntBuilder()
        ant.unjar(src: "$groovyProjectDir/build/$GROOVY_PROJECT_NAME-1.0.jar", dest: unjarPath)
        assert new File("$unjarPath/META-INF/MANIFEST.MF").text.contains('myprop: myvalue')
        assert new File("$unjarPath/META-INF/myfile").isFile()

        // This test is also important for test cleanup
        Executer.execute(gradleHome, groovyProjectDir.absolutePath, ['clean'], '', Executer.DEBUG)
        assert !(new File(groovyProjectDir, "build").exists())
    }

    static void main(String[] args) {
        execute(args[0], args[1])
    }


}
