/**
 * 
 */
package com.sitescape.team.util;

import java.util.Observable;

/**
 * An interface for {@link Object}s which generate events of type
 * <code>E</code>. This interface is very similar to {@link Observable} but
 * uses generics to maintain type-safety.
 * 
 * @author dml
 * 
 * @param <S> -
 *            the type of the event source
 * @param <E> -
 *            the type of events generated by this <code>EventSource</code>
 */
public interface EventSource<S extends EventSource<S, E>, E> {

	public void register(EventListener<S, E> listener);
}
