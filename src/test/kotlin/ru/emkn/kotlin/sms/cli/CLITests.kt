package ru.emkn.kotlin.sms.cli

import ru.emkn.kotlin.sms.DEFAULT_ROUTE_PROTOCOL_TYPE
import ru.emkn.kotlin.sms.ProgramSubcommands
import ru.emkn.kotlin.sms.RouteProtocolType
import java.io.File
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

internal class CLITests {

    private val testDataDir = "test-data/cli"

    @BeforeTest
    fun setUp() {
        val noExitSecurityManager = NoExitSecurityManager()
        System.setSecurityManager(noExitSecurityManager)
    }

    @Test
    fun `CsvFileArgType properly extracts csv files`() {
        assertEquals(
            listOf("foo.csv").map { File(it) }.toSet(),
            CsvFileListArgType.convert("foo.csv", "").toSet(),
        )
        assertEquals(
            listOf("file1.csv", "xls.csv.csv").map { File(it) }.toSet(),
            CsvFileListArgType.convert(
                "file1.csv,file2.rar,file3,file4.dbf,xls.csv.csv",
                ""
            ).toSet(),
        )
        assertEquals(
            listOf(
                "app1.csv",
                "$testDataDir/manyCsvs/file01.csv",
                "$testDataDir/manyCsvs/file02.csv",
                "$testDataDir/manyCsvs/file03.csv",
                "$testDataDir/manyCsvs/subfolder/app.csv",
            ).map { File(it) }.toSet(),
            CsvFileListArgType.convert(
                "test.txt,app1.csv,$testDataDir/manyCsvs/,$testDataDir/emptyFolder,aaa",
                ""
            ).toSet(),
        )
    }


    @Test
    fun `Empty args`() {
        assertFails {
            val argParsingSystem = ArgParsingSystem()
            argParsingSystem.parse(arrayOf())
        }
    }

    @Test
    fun `Invalid subcommands`() {
        assertFails {
            val argParsingSystem = ArgParsingSystem()
            argParsingSystem.parse(arrayOf("destroy", "cfg.json"))
        }
        assertFails {
            val argParsingSystem = ArgParsingSystem()
            argParsingSystem.parse(arrayOf("cfg.json", "make"))
        }
        assertFails {
            val argParsingSystem = ArgParsingSystem()
            argParsingSystem.parse(arrayOf("something"))
        }
        assertFails {
            val argParsingSystem = ArgParsingSystem()
            argParsingSystem.parse(arrayOf("configFile.json"))
        }
    }

    private fun testConfigFileAndOutputDirectory(
        args: Array<String>,
        expectedConfigFileName: String,
        expectedOutputDirectoryPath: String
    ) {
        val argParsingSystem = ArgParsingSystem()
        argParsingSystem.parse(args)
        assertEquals(
            File(expectedConfigFileName),
            argParsingSystem.competitionConfigDirectory
        )
        assertEquals(
            File(expectedOutputDirectoryPath),
            argParsingSystem.outputDirectory
        )
    }

    @Test
    fun `Config file and output directory`() {
        testConfigFileAndOutputDirectory(
            arrayOf("start", "cfg.json", "-o", "out", "-a", ""),
            expectedConfigFileName = "cfg.json",
            expectedOutputDirectoryPath = "out",
        )
        testConfigFileAndOutputDirectory(
            arrayOf(
                "cfg1.json",
                "result",
                "-o",
                "out",
                "-p",
                "",
                "-r",
                "",
                "-s",
                ""
            ),
            expectedConfigFileName = "cfg1.json",
            expectedOutputDirectoryPath = "out",
        )
        testConfigFileAndOutputDirectory(
            arrayOf(
                "result_teams",
                "cfg.json",
                "--output",
                "out1",
                "-p",
                "",
                "-r",
                ""
            ),
            expectedConfigFileName = "cfg.json",
            expectedOutputDirectoryPath = "out1",
        )
        assertFails {
            testConfigFileAndOutputDirectory(
                arrayOf("start", "cfg.json", "-a", "app.csv"),
                expectedConfigFileName = "cfg.json",
                expectedOutputDirectoryPath = "",
            )
        }
        assertFails {
            testConfigFileAndOutputDirectory(
                arrayOf("start", "-o", "out", "-a", "app.csv"),
                expectedConfigFileName = "cfg.json",
                expectedOutputDirectoryPath = "",
            )
        }
    }

    private fun testStartSubcommand(
        args: Array<String>,
        expectedApplicationFileNames: List<String>
    ) {
        val expectedApplicationFiles =
            expectedApplicationFileNames.map { File(it) }

        val argParsingSystem = ArgParsingSystem()
        argParsingSystem.parse(args)
        assertEquals(
            ProgramSubcommands.START,
            argParsingSystem.invokedSubcommand
        )
        assertEquals(
            expectedApplicationFiles,
            argParsingSystem.startCommand.applicationFiles
        )
    }

    @Test
    fun `Start subcommand`() {
        testStartSubcommand(
            arrayOf("start", "cfg.json", "-o", "out", "-a", "app.csv"),
            expectedApplicationFileNames = listOf("app.csv"),
        )
        testStartSubcommand(
            arrayOf(
                "start",
                "cfg.json",
                "-o",
                "out",
                "--applications",
                "app1.csv,app2.csv"
            ),
            expectedApplicationFileNames = listOf("app1.csv", "app2.csv"),
        )
        assertFails {
            testStartSubcommand(
                arrayOf("start", "cfg.json", "-o", "out"),
                expectedApplicationFileNames = listOf(),
            )
        }
        assertFails {
            testStartSubcommand(
                arrayOf("start", "cfg.json", "-o", "out", "-w", "123", "456"),
                expectedApplicationFileNames = listOf(),
            )
        }
    }

    private fun testResultSubcommand(
        args: Array<String>,
        expectedParticipantsListFileName: String,
        expectedStartingProtocolFileNames: List<String>,
        expectedRouteProtocolType: RouteProtocolType,
        expectedRouteProtocolFileNames: List<String>,
    ) {
        val expectedStartingProtocolFiles =
            expectedStartingProtocolFileNames.map { File(it) }
        val expectedParticipantsListFile =
            File(expectedParticipantsListFileName)
        val expectedRouteProtocolFiles =
            expectedRouteProtocolFileNames.map { File(it) }

        val argParsingSystem = ArgParsingSystem()
        argParsingSystem.parse(args)
        assertEquals(
            ProgramSubcommands.RESULT,
            argParsingSystem.invokedSubcommand
        )
        assertEquals(
            expectedParticipantsListFile,
            argParsingSystem.resultCommand.participantListFile
        )
        assertEquals(
            expectedStartingProtocolFiles,
            argParsingSystem.resultCommand.startingProtocolFiles
        )
        assertEquals(
            expectedRouteProtocolType,
            argParsingSystem.resultCommand.routeProtocolType
        )
        assertEquals(
            expectedRouteProtocolFiles,
            argParsingSystem.resultCommand.routeProtocolFiles
        )
    }

    @Test
    fun `Result subcommand`() {
        testResultSubcommand(
            arrayOf(
                "result",
                "cfg.json",
                "-o",
                "out",
                "-p",
                "participantsList.csv",
                "-s",
                "startingProtocol.csv",
                "-r",
                "routeProtocol.csv"
            ),
            expectedParticipantsListFileName = "participantsList.csv",
            expectedStartingProtocolFileNames = listOf(
                "startingProtocol.csv",
            ),
            expectedRouteProtocolType = DEFAULT_ROUTE_PROTOCOL_TYPE,
            expectedRouteProtocolFileNames = listOf(
                "routeProtocol.csv",
            )
        )
        testResultSubcommand(
            arrayOf(
                "result",
                "cfg.json",
                "-o",
                "out",
                "-r",
                "routeProtocol.csv",
                "-p",
                "participantsList.csv",
                "-s",
                "startingProtocol.csv"
            ),
            expectedParticipantsListFileName = "participantsList.csv",
            expectedStartingProtocolFileNames = listOf(
                "startingProtocol.csv",
            ),
            expectedRouteProtocolType = DEFAULT_ROUTE_PROTOCOL_TYPE,
            expectedRouteProtocolFileNames = listOf(
                "routeProtocol.csv",
            )
        )
        testResultSubcommand(
            arrayOf(
                "result",
                "cfg.json",
                "-o",
                "out",
                "--startingProtocols",
                "startingProtocol.csv",
                "--participants",
                "participantsList.csv",
                "--routeProtocols",
                "protocol1.csv,protocol2.csv",
                "-tp",
                "OF_PARTICIPANT"
            ),
            expectedParticipantsListFileName = "participantsList.csv",
            expectedStartingProtocolFileNames = listOf(
                "startingProtocol.csv",
            ),
            expectedRouteProtocolType = RouteProtocolType.OF_PARTICIPANT,
            expectedRouteProtocolFileNames = listOf(
                "protocol1.csv",
                "protocol2.csv",
            )
        )
        testResultSubcommand(
            arrayOf(
                "result",
                "cfg.json",
                "-s",
                "sprotocol.csv",
                "-o",
                "out",
                "--routeProtocolType",
                "of_checkpoint",
                "-p",
                "plist.csv",
                "-r",
                "rprotocol.csv"
            ),
            expectedParticipantsListFileName = "plist.csv",
            expectedStartingProtocolFileNames = listOf(
                "sprotocol.csv",
            ),
            expectedRouteProtocolType = RouteProtocolType.OF_CHECKPOINT,
            expectedRouteProtocolFileNames = listOf(
                "rprotocol.csv",
            )
        )
        assertFails {
            testResultSubcommand(
                arrayOf(
                    "result",
                    "cfg.json",
                    "-o",
                    "out",
                    "-p",
                    "plist.csv",
                    "-s",
                    "sprotocol.csv"
                ),
                expectedParticipantsListFileName = "plist.csv",
                expectedStartingProtocolFileNames = listOf(
                    "sprotocol.csv",
                ),
                expectedRouteProtocolType = DEFAULT_ROUTE_PROTOCOL_TYPE,
                expectedRouteProtocolFileNames = listOf()
            )
        }
        assertFails {
            testResultSubcommand(
                arrayOf(
                    "result",
                    "cfg.json",
                    "-o",
                    "out",
                    "-r",
                    "protocol.csv",
                    "-s",
                    "sprotocol.csv"
                ),
                expectedParticipantsListFileName = "",
                expectedStartingProtocolFileNames = listOf(
                    "sprotocol.csv",
                ),
                expectedRouteProtocolType = DEFAULT_ROUTE_PROTOCOL_TYPE,
                expectedRouteProtocolFileNames = listOf(
                    "protocol.csv"
                )
            )
        }
        assertFails {
            testResultSubcommand(
                arrayOf(
                    "result",
                    "cfg.json",
                    "-o",
                    "out",
                    "-p",
                    "plist.csv",
                    "-r",
                    "protocol.csv",
                    "-s",
                    "sprotocol.csv",
                    "-tp",
                    "invalid_type"
                ),
                expectedParticipantsListFileName = "plist.csv",
                expectedStartingProtocolFileNames = listOf(
                    "sprotocol.csv",
                ),
                expectedRouteProtocolType = DEFAULT_ROUTE_PROTOCOL_TYPE,
                expectedRouteProtocolFileNames = listOf(
                    "protocol.csv"
                )
            )
        }
        assertFails {
            testResultSubcommand(
                arrayOf(
                    "result",
                    "cfg.json",
                    "-o",
                    "out",
                    "-w",
                    "plist.csv",
                    "-r",
                    "protocol.csv",
                    "-s",
                    "sprotocol.csv",
                    "-tp",
                    "invalid_type"
                ),
                expectedParticipantsListFileName = "plist.csv",
                expectedStartingProtocolFileNames = listOf(
                    "sprotocol.csv",
                ),
                expectedRouteProtocolType = DEFAULT_ROUTE_PROTOCOL_TYPE,
                expectedRouteProtocolFileNames = listOf(
                    "protocol.csv"
                )
            )
        }
        assertFails {
            testResultSubcommand(
                arrayOf(
                    "result",
                    "cfg.json",
                    "-o",
                    "out",
                    "-p",
                    "participantsList.csv",
                    "-r",
                    "routeProtocol.csv"
                ),
                expectedParticipantsListFileName = "participantsList.csv",
                expectedStartingProtocolFileNames = listOf(),
                expectedRouteProtocolType = DEFAULT_ROUTE_PROTOCOL_TYPE,
                expectedRouteProtocolFileNames = listOf(
                    "routeProtocol.csv",
                )
            )
        }
    }


    private fun testResultTeamsSubcommand(
        args: Array<String>,
        expectedParticipantsListFileName: String,
        expectedResultProtocolFileNames: List<String>,
    ) {
        val expectedParticipantsListFile =
            File(expectedParticipantsListFileName)
        val expectedResultProtocolFiles =
            expectedResultProtocolFileNames.map { File(it) }

        val argParsingSystem = ArgParsingSystem()
        argParsingSystem.parse(args)
        assertEquals(
            ProgramSubcommands.RESULT_TEAMS,
            argParsingSystem.invokedSubcommand
        )
        assertEquals(
            expectedParticipantsListFile,
            argParsingSystem.resultTeamsCommand.participantListFile
        )
        assertEquals(
            expectedResultProtocolFiles,
            argParsingSystem.resultTeamsCommand.resultProtocolFiles
        )
    }

    @Test
    fun `Result teams subcommand`() {
        testResultTeamsSubcommand(
            arrayOf(
                "result_teams",
                "cfg.json",
                "-o",
                "out",
                "-p",
                "participantsList.csv",
                "-r",
                "resultProtocol1.csv,resultProtocol2.csv"
            ),
            expectedParticipantsListFileName = "participantsList.csv",
            expectedResultProtocolFileNames = listOf(
                "resultProtocol1.csv",
                "resultProtocol2.csv",
            )
        )
        testResultTeamsSubcommand(
            arrayOf(
                "result_teams",
                "cfg.json",
                "-o",
                "out",
                "--resultProtocols",
                "resProtocol.csv",
                "--participants",
                "participantsList.csv"
            ),
            expectedParticipantsListFileName = "participantsList.csv",
            expectedResultProtocolFileNames = listOf(
                "resProtocol.csv",
            )
        )
        assertFails {
            testResultTeamsSubcommand(
                arrayOf(
                    "result_teams",
                    "cfg.json",
                    "-o",
                    "out",
                    "-p",
                    "participantsList.csv"
                ),
                expectedParticipantsListFileName = "participantsList.csv",
                expectedResultProtocolFileNames = listOf()
            )
        }
        assertFails {
            testResultTeamsSubcommand(
                arrayOf(
                    "result_teams",
                    "cfg.json",
                    "-o",
                    "out",
                    "-r",
                    "rprotocol.csv"
                ),
                expectedParticipantsListFileName = "",
                expectedResultProtocolFileNames = listOf(
                    "rprotocol.csv",
                )
            )
        }
        assertFails {
            testResultTeamsSubcommand(
                arrayOf(
                    "result_teams",
                    "cfg.json",
                    "-o",
                    "out",
                    "-test",
                    "something   temp",
                    "baz"
                ),
                expectedParticipantsListFileName = "",
                expectedResultProtocolFileNames = listOf()
            )
        }
    }
}