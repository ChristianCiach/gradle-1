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

package org.gradle.api.tasks.bundling

import org.gradle.api.tasks.util.FileSet
import org.gradle.api.tasks.util.Files

/**
 * @author Hans Dockter
 */
class AntWarTest extends AbstractAntArchiveTest {
    static final String FILESET_CLASSES_KEY = 'myclasses'
    static final String WEBINFS_KEY = 'mywebinfs'
    static final String ADDITIONAL_LIBS_KEY = 'myadditionallibs'
    static final String EXPLICIT_WEB_XML_TEXT = 'exlicitwebxmltext'
    static final String IMPLICIT_WEB_XML_TEXT = 'imlicitwebxmltext'

    Files libFiles

    File explicitWebXml
    File implicitWebXml

    AntWar antWar

    void setUp() {
        super.setUp()
        antWar = new AntWar()
        libFiles = new Files()
        libFiles.files = [new File(testDir, 'libFile1'), new File(testDir, 'libFile2')]
        libFiles.files*.createNewFile()
        (explicitWebXml = new File(testDir, 'myweb.xml')).write(EXPLICIT_WEB_XML_TEXT)
        (implicitWebXml = new File(testDir, 'web.xml')).write(IMPLICIT_WEB_XML_TEXT)
    }

    void testExecute() {
        antWar.execute(new AntMetaArchiveParameter(resourceCollections, true, testDir, archiveName, manifest,
                createFileSetDuo(AbstractAntArchiveTest.METAINFS_KEY), new AntBuilder()),
                createFileSetDuo(FILESET_CLASSES_KEY),
                libFiles,
                createFileSetDuo(ADDITIONAL_LIBS_KEY),
                createFileSetDuo(WEBINFS_KEY),
                explicitWebXml
                )
        checkCommonData()
        assertTrue(new File(unzipDir, "WEB-INF/web.xml").exists())
        assertEquals(EXPLICIT_WEB_XML_TEXT, new File(unzipDir, "WEB-INF/web.xml").text)
    }

    void testExecuteWithImplicitWebxml() {
        FileSet fileSet = new FileSet(testDir)
        fileSet.include(implicitWebXml.name)
        antWar.execute(new AntMetaArchiveParameter(resourceCollections, true, testDir, archiveName, manifest,
                createFileSetDuo(AbstractAntArchiveTest.METAINFS_KEY), new AntBuilder()),
                createFileSetDuo(FILESET_CLASSES_KEY),
                libFiles,
                createFileSetDuo(ADDITIONAL_LIBS_KEY),
                createFileSetDuo(WEBINFS_KEY) << fileSet,
                null
                )
        checkCommonData()
        assertTrue(new File(unzipDir, "WEB-INF/web.xml").exists())
        assertEquals(IMPLICIT_WEB_XML_TEXT, new File(unzipDir, "WEB-INF/web.xml").text)
    }

    void testExecuteWithExplicitAndImplicitWebxml() {
        FileSet fileSet = new FileSet(testDir)
        fileSet.include(implicitWebXml.name)
        antWar.execute(new AntMetaArchiveParameter(resourceCollections, true, testDir, archiveName, manifest,
                createFileSetDuo(AbstractAntArchiveTest.METAINFS_KEY), new AntBuilder()),
                createFileSetDuo(FILESET_CLASSES_KEY),
                libFiles,
                createFileSetDuo(ADDITIONAL_LIBS_KEY),
                createFileSetDuo(WEBINFS_KEY) << fileSet,
                explicitWebXml
                )
        checkCommonData()
        assertTrue(new File(unzipDir, "WEB-INF/web.xml").exists())
        assertEquals(EXPLICIT_WEB_XML_TEXT, new File(unzipDir, "WEB-INF/web.xml").text)
    }

    void testExecuteWithNullLists() {
        antWar.execute(new AntMetaArchiveParameter(resourceCollections, true, testDir, archiveName, null,
                null, new AntBuilder()),
                null,
                null,
                null,
                null,
                explicitWebXml
                )
        unzipArchive()
        checkResourceFiles()
    }

    void checkCommonData() {
        unzipArchive()
        checkResourceFiles()
        checkMetaData()
        checkExistenceOfFileSetDuo(unzipDir, 'WEB-INF/classes/', FILESET_CLASSES_KEY)
        checkExistenceOfFileSetDuo(unzipDir, 'WEB-INF/', WEBINFS_KEY)
        checkExistenceOfFileSetDuo(unzipDir, 'WEB-INF/lib/', ADDITIONAL_LIBS_KEY)
    }


}