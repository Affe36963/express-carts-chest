package expresscarts;

/**
 * Marker interface implemented by every Express Carts entity (regular and chest minecarts).
 * Lets mixins that only need to know "is this one of ours" avoid an ever-growing list of
 * {@code instanceof} checks against each concrete Express Carts entity type.
 */
public interface ExpressCart {
}
