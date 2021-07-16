package sku.app.lib_tracker.test_utils

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class DisableAnimationRule : TestRule {

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                disableAnimations()
                try {
                    base.evaluate()
                } finally {
                    enableAnimations()
                }
            }

        }
    }

    private fun disableAnimations() {
        changeAnimationState(false)
    }

    private fun enableAnimations() {
        changeAnimationState(true)
    }

    private fun changeAnimationState(enable: Boolean) {
        with(UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())) {
            executeShellCommand("settings put global transition_animation_scale ${if (enable) 1 else 0}")
            executeShellCommand("settings put global window_animation_scale ${if (enable) 1 else 0}")
            executeShellCommand("settings put global animation_duration_scale ${if (enable) 1 else 0}")
        }
    }

}

// https://stackoverflow.com/questions/29908110/how-to-disable-animations-in-code-when-running-espresso-tests
// https://proandroiddev.com/one-rule-to-disable-them-all-d387da440318