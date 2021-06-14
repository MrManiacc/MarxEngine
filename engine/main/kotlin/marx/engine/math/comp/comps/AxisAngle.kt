package marx.engine.math.comp.comps

import marx.engine.math.comp.*

interface AxisAngle {

    object AxisX : Comp, AxisAngle {
        override val idx: Int = 0
    }

    object AxisY : Comp, AxisAngle {
        override val idx: Int = 1
    }

    object AxisZ : Comp, AxisAngle {
        override val idx: Int = 2
    }

    object Angle : Comp, AxisAngle {
        override val idx: Int = 3
    }
}