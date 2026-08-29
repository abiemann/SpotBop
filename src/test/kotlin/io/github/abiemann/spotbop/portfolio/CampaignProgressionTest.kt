package io.github.abiemann.spotbop.portfolio

import org.junit.Assert.assertEquals
import org.junit.Test

class CampaignProgressionTest {

    @Test
    fun campaignUsesThreeConsecutiveLevelsInEachScene() {
        val expectedScenes = LaunchScene.entries.flatMap { scene -> List(3) { scene } }
        val campaign = CampaignProgression()

        expectedScenes.forEachIndexed { index, expectedScene ->
            assertEquals(index + 1, campaign.currentLevelNumber)
            assertEquals(expectedScene, campaign.currentStage)
            val expectedOutcome = if (index == expectedScenes.lastIndex) {
                CampaignOutcome.COMPLETED
            } else {
                CampaignOutcome.NEXT
            }
            assertEquals(expectedOutcome, campaign.finishLevel(10 + index, 10))
        }

        assertEquals(255, campaign.totalScore)
        assertEquals(15, campaign.currentLevelNumber)
        assertEquals(LaunchScene.SPACE, campaign.currentStage)
    }

    @Test
    fun bronzeThresholdIsInclusiveAndFailureDoesNotAdvance() {
        val campaign = CampaignProgression()

        assertEquals(CampaignOutcome.FAILED, campaign.finishLevel(9, 10))
        assertEquals(1, campaign.currentLevelNumber)

        campaign.reset()

        assertEquals(CampaignOutcome.NEXT, campaign.finishLevel(10, 10))
        assertEquals(2, campaign.currentLevelNumber)
    }

    @Test
    fun failureIncludesCurrentScoreInCampaignTotal() {
        val campaign = CampaignProgression()

        assertEquals(CampaignOutcome.NEXT, campaign.finishLevel(17, 10))
        assertEquals(CampaignOutcome.NEXT, campaign.finishLevel(16, 10))
        assertEquals(CampaignOutcome.FAILED, campaign.finishLevel(9, 10))

        assertEquals(42, campaign.totalScore)
        assertEquals(3, campaign.currentLevelNumber)
    }

    @Test
    fun selectedSceneStartsAtItsFirstLevelAndResetRestoresIt() {
        LaunchScene.entries.forEachIndexed { stageIndex, scene ->
            val campaign = CampaignProgression(stageIndex)

            assertEquals(stageIndex * 3 + 1, campaign.currentLevelNumber)
            assertEquals(scene, campaign.currentStage)
            campaign.finishLevel(12, 10)
            campaign.reset()

            assertEquals(stageIndex * 3 + 1, campaign.currentLevelNumber)
            assertEquals(0, campaign.totalScore)
        }
    }

    @Test
    fun treasureBonusCommitsCombinedScoreExactlyOnce() {
        val campaign = CampaignProgression(startingStageIndex = 1)

        assertEquals(4, campaign.currentLevelNumber)
        assertEquals(CampaignOutcome.NEXT, campaign.finishTreasureBonus(73))
        assertEquals(5, campaign.currentLevelNumber)
        assertEquals(73, campaign.totalScore)
    }

    @Test(expected = IllegalArgumentException::class)
    fun treasureBonusCannotReplaceAFieldLevel() {
        CampaignProgression(startingStageIndex = 2).finishTreasureBonus(100)
    }
}
