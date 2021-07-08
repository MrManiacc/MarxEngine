package marx.assets

/**
 * This interface must be implemented but does not impose any requirements beyond that. [AssetData] is used as a marker
 * and should be used to pass any data needed to create an instance of something. For example we maybe have some material/
 * shader that takes parameters such as texture input, or float input, for things like diffuse, ambient, normals, etc.
 * It acts a medium between the asset type and the asset instance.
 */
interface AssetData