package marx.engine.math.comp.comps

import marx.engine.math.comp.*

interface Scale {

    object Width : Comp, Scale {
        override val idx: Int = 0
    }

    object Height : Comp, Scale {
        override val idx: Int = 1
    }

    object Depth : Comp, Scale {
        override val idx: Int = 2
    }
}