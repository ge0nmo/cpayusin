package com.cpayusin.file.repository;

import com.cpayusin.file.domain.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileJpaRepository extends JpaRepository<File, Long>
{

}
