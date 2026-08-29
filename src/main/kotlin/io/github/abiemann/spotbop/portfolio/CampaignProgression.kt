package io.github.abiemann.spotbop.portfolio

internal enum class LaunchScene {
    UNDERWATER,
    BEACH,
    LAND,
    CLOUDS,
    SPACE,
}

internal val CAMPAIGN_SCENES = LaunchScene.entries
internal const val CAMPAIGN_LEVELS_PER_SCENE = 3
internal val CAMPAIGN_LEVEL_COUNT = CAMPAIGN_SCENES.size * CAMPAIGN_LEVELS_PER_SCENE

internal enum class CampaignDifficulty {
    EASY,
    MEDIUM,
    HARD,
}

internal fun campaignDifficultyForLevelIndex(levelIndex: Int): CampaignDifficulty {
    require(levelIndex in 0 until CAMPAIGN_LEVEL_COUNT)
    return when (levelIndex % CAMPAIGN_LEVELS_PER_SCENE) {
        0 -> CampaignDifficulty.EASY
        1 -> CampaignDifficulty.MEDIUM
        else -> CampaignDifficulty.HARD
    }
}

internal enum class CampaignOutcome {
    NEXT,
    FAILED,
    COMPLETED,
}

/** Pure state for three consecutive levels in each of five campaign scenes. */
internal class CampaignProgression(
    val startingStageIndex: Int = 0,
) {
    private val startingLevelIndex = startingStageIndex * CAMPAIGN_LEVELS_PER_SCENE

    var currentLevelIndex: Int = startingLevelIndex
        private set

    var totalScore: Int = 0
        private set

    val currentLevelNumber: Int
        get() = currentLevelIndex + 1

    val currentStageIndex: Int
        get() = currentLevelIndex / CAMPAIGN_LEVELS_PER_SCENE

    val currentStage: LaunchScene
        get() = CAMPAIGN_SCENES[currentStageIndex]

    val currentDifficulty: CampaignDifficulty
        get() = campaignDifficultyForLevelIndex(currentLevelIndex)

    init {
        require(startingStageIndex in CAMPAIGN_SCENES.indices) {
            "campaign starting stage is out of range"
        }
    }

    fun finishLevel(score: Int, bronzeThreshold: Int): CampaignOutcome {
        totalScore += score
        if (score < bronzeThreshold) return CampaignOutcome.FAILED
        if (currentLevelIndex == CAMPAIGN_LEVEL_COUNT - 1) {
            return CampaignOutcome.COMPLETED
        }

        currentLevelIndex++
        return CampaignOutcome.NEXT
    }

    /** Commits the combined Beach-and-bonus score once, then advances. */
    fun finishTreasureBonus(score: Int): CampaignOutcome {
        require(currentLevelIndex in TREASURE_LEVEL_4_INDEX..TREASURE_LEVEL_6_INDEX) {
            "treasure bonus is only available from Beach levels 4 through 6"
        }
        totalScore += score
        currentLevelIndex++
        return CampaignOutcome.NEXT
    }

    fun reset() {
        currentLevelIndex = startingLevelIndex
        totalScore = 0
    }
}

private const val TREASURE_LEVEL_4_INDEX = 3
private const val TREASURE_LEVEL_6_INDEX = 5
