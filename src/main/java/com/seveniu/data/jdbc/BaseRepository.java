package com.seveniu.data.jdbc;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by seveniu on 8/19/16.
 */
public interface BaseRepository<T> extends JpaRepository<T,Integer>{
}
