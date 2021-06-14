package marx.engine.math.comp.comps

import marx.engine.math.comp.*

interface Color {

    object R : Comp, Color {
        override val idx: Int = 0
    }

    object G : Comp, Color {
        override val idx: Int = 1
    }

    object B : Comp, Color {
        override val idx: Int = 2
    }

    object A : Comp, Color {
        override val idx: Int = 3
    }

}