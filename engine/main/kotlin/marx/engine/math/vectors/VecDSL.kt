package marx.engine.math.vectors

import marx.engine.math.*
import marx.engine.math.comp.*
import marx.engine.math.comp.comps.AxisAngle.*
import marx.engine.math.comp.comps.Position.*
import marx.engine.math.comp.comps.Rotation.*
import marx.engine.math.rotations.*
import marx.engine.math.comp.comps.Scale.*
import org.joml.*

/**
 * This stores all of the domain specific language related things for joml classes.
 * That means it's purposes is to provide extensions to joml classes
 */
object VecDSL {

    object Conversions {

        /**Only a transform is a transform. No direct conversions from vectors**/
        val IVec<*>.isTransform: Boolean get() = this is Transform

        /**True if this vector2f**/
        val IVec<*>.isVec2: Boolean
            get() = this is Vector2fc ||
                    (this[X] != null && this[Y] != null)

        /**True if this rotational vector for rotation**/
        val IVec<*>.isRot2: Boolean
            get() = (this[Pitch] != null && this[Yaw] != null)

        /**True if this vector3f**/
        val IVec<*>.isVec3: Boolean
            get() = this is Vector3fc ||
                    (this[X] != null && this[Y] != null && this[Z] != null)

        /**True if this rotational vector for rotation**/
        val IVec<*>.isRot3: Boolean
            get() = (this[Pitch] != null && this[Yaw] != null && this[Yaw] != null)

        /**True if this vector4f**/
        val IVec<*>.isVec4: Boolean
            get() = this is Vector4fc ||
                    (this[X] != null && this[Y] != null && this[Z] != null && this[W] != null)

        /**True if this Angle4 ([AxisAngle4f])**/
        val IVec<*>.isAngle4: Boolean
            get() = this is AxisAngle4f ||
                    (this[X] != null && this[Y] != null && this[Z] != null && this[W] != null)

        val IVec<*>.vec2: Vec2
            get() = if (this is Vec2) this
            else Vec2(this[X, 0f], this[Y, 0f])

        val IVec<*>.rot2: Vec2
            get() = if (this is Vec2) this
            else Vec2(this[Pitch, this[X, 0f]], this[Yaw, this[Y, 0f]])

        val IVec<*>.vec3: Vec3
            get() = if (this is Vec3) this
            else Vec3(this[X, 0f], this[Y, 0f], this[Z, 0f])

        val IVec<*>.rot3: Vec3
            get() = if (this is Vec3) this
            else Vec3(this[Pitch, this[X, 0f]], this[Yaw, this[Y, 0f]], this[Roll, this[Z, 0f]])

        val IVec<*>.scale3: Vec3
            get() = if (this is Vec3) this
            else Vec3(this[Width, this[X, 0f]], this[Height, this[Y, 0f]], this[Depth, this[Z, 0f]])

        val IVec<*>.vec4: Vec4
            get() = if (this is Vec4) this
            else Vec4(this[X, 0f], this[Y, 0f], this[Z, 0f], this[X, 0f])

        /**
         * This will generate a new angle from this vector
         */
        val IVec<*>.angle4: Angle4
            get() = if (this is Angle4) this
            else Angle4(
                this[AxisX, this[Pitch, this[X, 0f]]],
                this[AxisY, this[Yaw, this[Y, 0f]]],
                this[AxisZ, this[Roll, this[Z, 0f]]],
                this[Angle, this[W, 0f]]
            )

        /**Creates a quaternion from this vector instance**/
        val IVec<*>.quaternion: Quaternionf
            get() = Quaternionf(this.angle4)

        /*Converts the given vector to a transform**/
        val IVec<*>.transform: Transform get() = if (this is Transform) this else Transform(vec3, rot3, scale3)
    }

    /**
     * This stores various inline extensions for the different vectors
     */
    object Extensions {
        /**
         * Provides an extension for creating a vec2 via 2 numbers
         */
        infix fun Number.and(other: Number): Vec2 = Vec2(this, other)

        /**
         * Creates a vec3 from a vec2 with a number
         */
        infix fun Vec2.and(other: Number): Vec3 = Vec3(this, other.toFloat())

        /**
         * Creates a vec3 from a vec2 with a number
         */
        infix fun Vec3.and(other: Number): Vec4 = Vec4(this, other.toFloat())

        /**
         * This adds our get refined get method so we can do [IVec<X>]
         */
        inline operator fun <reified COMP : Comp> IVec<*>.get(default: () -> Float) = compOr<COMP>(default)

        /**
         * This adds our get refined get method so we can do [IVec<X>]
         */
        inline fun <reified COMP : Comp> IVec<*>.comp() = get(COMP::class.objectInstance!!)

        /**
         * This adds our get refined get method so we can do [IVec<X>]
         */
        inline fun <reified COMP : Comp> IVec<*>.compOr(default: Float) = compOr<COMP> { default }

        /**
         * This adds our get refined get method so we can do [IVec<X>]
         */
        inline fun <reified COMP : Comp> IVec<*>.compOr(default: () -> Float) = comp<COMP>() ?: default()

    }


}