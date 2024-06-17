package br.edu.utfpr.servicebook.model.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;

/**
 * Classe que representa um serviço adicionado por um profissional ao seu portfólio.
 * Entre os serviços cadastrados pelo administrador, ele escolhe quais ele oferece.
 * Mesmo que o serviço cadastrado pelo administrador tenha uma descrição, ele pode customizar a descrição do serviço que ele oferece.
 * Ele pode ter várias customizações para um mesmo serviço cadastrado pelo Administrador.
 */
@Data
    @Table(name = "professional_service_offerings")
@NoArgsConstructor
@Entity
public class ProfessionalServiceOffering {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public enum Type {
        INDIVIDUAL,
        COMBINED_PACKAGE,
        SIMPLE_PACKAGE
    }

    /**
     * Tipo de anúncio.
     */
    @Enumerated(EnumType.STRING)
    private ProfessionalServiceOffering.Type type;


    /**
     * Nome do serviço.
     * O profissional pode customizar o nome do serviço cadastrado pelo administrador de várias maneiras, dependendo
     * do tipo de particularidade que o seu serviço tem.
     * Caso ele não especifique o nome do serviço, o nome do serviço cadastrado pelo administrador será usado.
     * O título do serviço deve ser único.
     */
    @Column(unique = true)
    private String name;

    /**
     * Descrição do serviço em primeira pessoa.
     * O profissional pode customizar a descrição do serviço cadastrado pelo administrador de várias maneiras, dependendo
     * do tipo de particularidade que o seu serviço tem.
     * Caso ele não especifique a descrição do serviço, a descrição do serviço cadastrado pelo administrador será usada.
     */
    private String description;

    /**
     * Preço do serviço individual.
     */
    private Long price;

    /**
     * Unidade de preço do serviço.
     */
    private String unit;

    /**
     * Duração do serviço.
     */
    private String duration;

    /**
     * Serviço cadastrado pelo administrador.
     * O profissional pode customizar a descrição do serviço cadastrado pelo administrador de várias maneiras, dependendo
     * do tipo de particularidade que o seu serviço tem.
     */
    @ManyToOne
    @JoinColumn(name = "service_id")
    private Service service;

    /**
     * Especialidade do anúncio.
     */
    @ManyToOne
    private Expertise expertise;

    /**
     * Profissional que oferece o serviço.
     */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
