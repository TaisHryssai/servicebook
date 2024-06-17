package br.edu.utfpr.servicebook.model.repository;

import br.edu.utfpr.servicebook.model.entity.JobImages;
import br.edu.utfpr.servicebook.model.entity.JobRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobImagesRepository extends JpaRepository<JobImages, Long> {

    List<JobImages> findJobImagesByJobRequest(JobRequest jobRequest);
}