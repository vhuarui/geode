/*=========================================================================
 * Copyright (c) 2002-2014 Pivotal Software, Inc. All Rights Reserved.
 * This product is protected by U.S. and international copyright
 * and intellectual property laws. Pivotal products are covered by
 * more patents listed at http://www.pivotal.io/patents.
 *=========================================================================
 */
package com.gemstone.gemfire.distributed.internal.membership;

import com.gemstone.gemfire.distributed.Role;
import com.gemstone.gemfire.distributed.internal.DM;
import com.gemstone.gemfire.distributed.internal.InternalDistributedSystem;
import com.gemstone.gemfire.internal.i18n.LocalizedStrings;

import java.util.*;

/**
 * <p>Members of the distributed system can fill one or more user defined 
 * roles. A role is metadata that describes how the member relates to other 
 * members or what purpose it fills.</p>
 * 
 * <p>This class should not be Serializable or DataSerializable. It has a
 * private constructor and it maintains a static canonical map of instances.
 * Any serializable object which has instances of InternalRole should flag
 * those variables as transient. Objects that implement DataSerializable
 * should convert the roles to String names. For an example, please see
 * {@link com.gemstone.gemfire.cache.MembershipAttributes}.</p>
 *
 * <p>Serializable classes which hold references to Roles should customize
 * serialization to transfer string names for Roles. See {@link 
 * com.gemstone.gemfire.cache.RegionAccessException RegionAccessException}
 * and {@link com.gemstone.gemfire.cache.RegionDistributionException 
 * RegionDistributionException} for examples on how to do this.</p>
 *
 * @author Kirk Lund
 * @since 5.0
 */
public class InternalRole implements Role {
  
  /** The name of this role */
  private final String name;

  /** Static canonical instances of roles. key=name, value=InternalRole */  
  private static final Map roles = new HashMap(); // could use ConcurrentHashMap
  
  /** Contructs a new InternalRole instance for the specified role name */
  private InternalRole(String name) {
    this.name = name;
  }
  
  public String getName() {
    return this.name;
  }
  
  /**
   * implements the java.lang.Comparable interface
   * 
   * @see java.lang.Comparable
   * @param o 
   *        the Object to be compared
   * @return a negative integer, zero, or a positive integer as this object is
   *         less than, equal to, or greater than the specified object.
   * @exception java.lang.ClassCastException
   *            if the specified object's type prevents it from being compared
   *            to this Object.
   */
  public int compareTo(Role o) {
    if ((o == null) || !(o instanceof InternalRole)) {
      throw new ClassCastException(LocalizedStrings.InternalRole_INTERNALROLECOMPARETO_COMPARISON_BETWEEN_DIFFERENT_CLASSES.toLocalizedString());
    }
    InternalRole other = (InternalRole) o;
    return this.name.compareTo(other.name);
  }
   
	/**
	 * Indicates whether some other object is "equal to" this one.
	 *
	 * @param  other  the reference object with which to compare.
	 * @return true if this object is the same as the obj argument;
	 *         false otherwise.
	 */
  @Override
	public boolean equals(Object other) {
		if (other == this) return true;
		if (other == null) return false;
		if (!(other instanceof InternalRole)) return  false;
		final InternalRole that = (InternalRole) other;

		if (this.name != that.name &&
	  		!(this.name != null &&
	  		this.name.equals(that.name))) return false;

		return true;
	}

	/**
	 * Returns a hash code for the object. This method is supported for the
	 * benefit of hashtables such as those provided by java.util.Hashtable.
	 *
	 * @return the integer 0 if description is null; otherwise a unique integer.
	 */
  @Override
	public int hashCode() {
		int result = 17;
		final int mult = 37;

		result = mult * result + 
			(this.name == null ? 0 : this.name.hashCode());

		return result;
	}
  
  /** 
   * Factory method to allow canonicalization of instances. Returns existing
   * instance or creates a new instance.
   */
  public static InternalRole getRole(String name) {
    synchronized (roles) {
      InternalRole role = (InternalRole) roles.get(name);
      if (role == null) {
        role = new InternalRole(name);
        roles.put(name, role);
      }
      return role;
    }
  }
  
  public boolean isPresent() {
    InternalDistributedSystem sys = 
        InternalDistributedSystem.getAnyInstance();
    if (sys == null) {
      throw new IllegalStateException(LocalizedStrings.InternalRole_ISPRESENT_REQUIRES_A_CONNECTION_TO_THE_DISTRIBUTED_SYSTEM.toLocalizedString());
    }
    DM dm = sys.getDistributionManager();
    return dm.isRolePresent(this);
  }
  
  public int getCount() {
    InternalDistributedSystem sys = 
        InternalDistributedSystem.getAnyInstance();
    if (sys == null) {
      throw new IllegalStateException(LocalizedStrings.InternalRole_GETCOUNT_REQUIRES_A_CONNECTION_TO_THE_DISTRIBUTED_SYSTEM.toLocalizedString());
    }
    DM dm = sys.getDistributionManager();
    return dm.getRoleCount(this);
  }

	/**
	 * Returns a string representation of the object.
	 * 
	 * @return a string representation of the object
	 */
  @Override
	public String toString() {
    return this.name;
	}
  
}

