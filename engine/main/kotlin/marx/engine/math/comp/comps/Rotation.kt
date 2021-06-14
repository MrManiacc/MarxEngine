package marx.engine.math.comp.comps

import marx.engine.math.comp.*

interface Rotation {
    object Yaw : Comp {
        override val idx: Int = 0
    }

    object Pitch : Comp {
        override val idx: Int = 1
    }

    object Roll : Comp {
        override val idx: Int = 2
    }
}