package at.forsyte.apalache.tla.lir.db

import at.forsyte.apalache.tla.lir.{UID, EID}
import collection.mutable.HashMap

/**
  * Basic database, storing a ValType for each KeyType.
  *
  * Provides an interface for data retrieval, but each subclass must implement storage on its own.
  */
abstract class DB[ KeyType, ValType ] {
  val m_name : String

  /** Retrieves the value associated with the key, if it exists in the database. */
  def get( key: KeyType ) : Option[ ValType ]
  /** Retrieves the value associated with the key, if it exists in the database, otherwise throws. */
  def apply( key: KeyType ) : ValType
  /** Retrieves the value associated with the key, if it exists in the database, otherwise returns `elseVal`. */
  def getOrElse( key: KeyType, elseVal : ValType ) : ValType = get( key ).getOrElse( elseVal )
  /** Returns the database size. */
  def size() : Int
  /** Checks whether the key exists in the database. */
  def contains( key : KeyType ) : Boolean
  /** Removes all entries from the database. */
  def clear() : Unit
  /** Debugging method, prints contents to std. output. */
  def print(): Unit

  def keySet() : Set[KeyType]

  def ==( p_map : Map[KeyType,ValType] ) : Boolean = {
    p_map.size == size && p_map.forall( pa => get( pa._1 ).contains( pa._2 ) )
  }
}

/**
  * Basic implementation of a DB wrapping a HashMap.
  */
abstract class HashMapDB[ KeyType, ValType ] extends DB[ KeyType, ValType ] {
  protected val m_map : HashMap[ KeyType, ValType ] = HashMap()
  def put( key: KeyType, value: ValType ) : Option[ValType] = {
    m_map.put( key, value )
  }
  def update( key: KeyType, value : ValType ) : Unit = {
    m_map.update( key, value )
  }
  override def get( key: KeyType ) : Option[ ValType ] = {
    return m_map.get( key )
  }
  override def apply( key : KeyType ) : ValType = {
    return m_map( key )
  }
  override def size() : Int = {
    return m_map.size
  }
  override def contains( key: KeyType ) : Boolean = {
    return m_map.contains( key )
  }
  def remove( key: KeyType ) : Unit = {
    m_map.remove( key )
  }
  override def clear() : Unit = {
    m_map.clear()
  }

  override def keySet() : Set[KeyType] = {
    m_map.keySet.toSet
  }

  override def print(): Unit = {
    println( "\n" + m_name + ": \n" )
    for ( ( k, v ) <- m_map ) {
      println( k + " -> " + v )
    }
  }
}


/**
  * A subclass of DB, which automatically calculates and stores the value associated with a given key.
  *
  * Emulates function memoization.
  */
abstract class SmartDB[ KeyType, ValType ] extends DB[ KeyType, ValType ] {

  /** Lookup that does not compute and store */
  def fetch( key: KeyType ) : Option[ ValType ]

  /** Computes the value of key, but does not store it */
  def evaluate( key : KeyType ) : Option[ ValType ]

  /**
    * Computes the value of the key and stores it.
    *
    * The method is protected, use apply() as entry point.
    */
  protected def evaluateAndSave( key: KeyType ) : Option[ ValType ]

  /** Lookup that computes and stores the value if it is not already stored */
  override def get( key: KeyType ) : Option[ ValType ] =  {
    val existing = fetch( key )
    /** If the key's value has already been calculated, return it. */
    if ( existing.nonEmpty ) {
      return existing
    }
    /** Otherwise, evaluate and store. */
    else{
      return evaluateAndSave( key )
    }
  }
}

abstract class SmartHashMapDB[ KeyType, ValType ] extends SmartDB[ KeyType, ValType ] {
  protected val map : HashMap[ KeyType, ValType ] = HashMap()

  override def fetch( key: KeyType ) : Option[ ValType ] = {
    return map.get( key )
  }

  protected def evaluateAndSave( key: KeyType ) : Option[ ValType ] ={
    val ret = evaluate( key )
    ret.foreach( map.put( key, _ ) )
    return ret
  }

  override def size() : Int = {
    return map.size
  }
  override def contains( key: KeyType ) : Boolean = {
    return map.contains( key )
  }
  def remove( key: KeyType ) : Unit = {
    map.remove( key )
  }
  override def clear() : Unit = {
    map.clear()
  }
  override def print(): Unit = {
    println( "\n" + m_name + ": \n" )
    for ( ( k, v ) <- map ) {
      println( k + " -> " + v )
    }
  }

}

