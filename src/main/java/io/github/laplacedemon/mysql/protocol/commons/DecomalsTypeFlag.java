package io.github.laplacedemon.mysql.protocol.commons;

/**
 * decimals (1) -- max shown decimal digits
 * @author shijiaqi
 *
 */
public interface DecomalsTypeFlag {
    byte STATIC_INTEGERS_AND_STRINGS = 0x00;
    byte DYNAMIC_VALUE = 0x1f;
}
