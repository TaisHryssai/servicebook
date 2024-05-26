package br.edu.utfpr.servicebook.model.repository;

import br.edu.utfpr.servicebook.model.dto.IndividualDTO;
import br.edu.utfpr.servicebook.model.dto.ProfessionalSearchItemDTO;
import br.edu.utfpr.servicebook.model.entity.Category;
import br.edu.utfpr.servicebook.model.entity.Expertise;
import br.edu.utfpr.servicebook.model.entity.Individual;
import br.edu.utfpr.servicebook.model.entity.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IndividualRepository extends JpaRepository<Individual, Long> {


    /**
     * Retorna o usuário por cpf.
     *
     * @param cpf
     * @return Optional<User>
     */
    Optional<Individual> findByCpf(String cpf);


    /**
     * Retorna o usuário por email.
     *
     * @param email
     * @return Optional<User>
     */
    Optional<Individual> findByEmail(String email);

    /**
     * Retorna o usuário por telefone.
     *
     * @param phoneNumber
     * @return Optional<User>
     */
    Optional<Individual> findByPhoneNumber(String phoneNumber);

    Optional<Individual> findByName(@Param("name") String name);

    @Query("select distinct p from Individual p left join ProfessionalExpertise pe on p.id = pe.professional.id where " +
            "lower(p.name) like lower(concat('%', :term, '%'))" +
            "or lower(p.description) like lower(concat('%', :term, '%')) " +
            "or lower(pe.expertise.name) like lower(concat('%', :term, '%'))")
    Page<Individual> findDistinctByTermIgnoreCaseWithPagination(
            String term,
            Pageable pageable);

    @Query("select distinct p from Individual p left join ProfessionalExpertise pe on p.id = pe.professional.id where " +
            "lower(p.name) like lower(concat('%', :term, '%'))" +
            "or lower(p.description) like lower(concat('%', :term, '%')) " +
            "or lower(pe.expertise.name) like lower(concat('%', :term, '%'))")
    List<Individual> findDistinctByTermIgnoreCase(String term);

    @Query("select p from Individual p left join ProfessionalServiceOffering pe on p.id = pe.user.id where " +
            "pe.service = :term")
    Page<Individual> findAllIndividualsByService(
            Service term,
            Pageable pageable);

    @Query("select p from Individual p left join ProfessionalExpertise pe on p.id = pe.professional.id  " +
            "where pe.expertise.id = 4")
    List<Individual> findAllIndividualsAutonomosByService(
            Long term,
            Pageable pageable);

    @Query("select distinct p from Individual p left join ProfessionalExpertise pe on p.id = pe.professional.id  " +
            " left  join Expertise ex on pe.expertise.id = ex.id "+
            "where pe.expertise = :expertiseId ")
    Page<Individual> findDistinctByExpertiseAndCategoryPagination(
            Expertise expertiseId,
            Pageable pageable);

    @Query("select distinct ex from Individual p left join ProfessionalExpertise pe on p.id = pe.professional.id  " +
            " left  join Expertise ex on pe.expertise.id = ex.id "+
            "where pe.expertise = :expertiseId or pe.expertise.category = :categoryId")
    Page<Individual> listByExpertiseAndCategory(
            Expertise expertiseId,
            Category categoryId,
            Pageable pageable);

}