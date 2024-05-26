<%@tag description="Servicebook - Banner template" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@attribute name="jobCandidate" type="br.edu.utfpr.servicebook.model.dto.JobCandidateDTO" %>

<a href="minha-conta/cliente/meus-pedidos/${jobRequest.id}/detalhes/${jobCandidate.id}">
    <div class="card-panel card-candidate">
        <div class="row ${(jobCandidate.chosenByBudget) ? 'primary-background-color-light': ''} no-margin">
            <div class="col s12 icons-area-request center">
                <div class="row">
                    <div class="col s12 center">
                        <div class=" candidate dark-color-text">
                            <c:forEach var="star" begin="1" end="5">
                                <c:if test="${star <= jobCandidate.user.rating}">
                                    <i class="material-icons yellow-text small">star</i>
                                </c:if>
                                <c:if test="${star > jobCandidate.user.rating}">
                                    <i class="material-icons dark-text small">star_border</i>
                                </c:if>
                            </c:forEach>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col s12 center">
                <c:if test="${jobCandidate.user.profilePicture == null}">
                    <svg style="width:120px;height:120px"
                         viewBox="0 0 24 24">
                        <path class="dark-color-icon"
                              d="M12,4A4,4 0 0,1 16,8A4,4 0 0,1 12,12A4,4 0 0,1 8,8A4,4 0 0,1 12,4M12,14C16.42,14 20,15.79 20,18V20H4V18C4,15.79 7.58,14 12,14Z"/>
                    </svg>
                </c:if>
                <c:if test="${jobCandidate.user.profilePicture != null}">
                    <div class="row">
                        <img src="${jobCandidate.user.profilePicture}"
                             alt="Profissional - Imagem de perfil."
                             style="width:110px;height:110px">
                    </div>
                </c:if>
            </div>
            <div class="col s12">
                <div class="center title-card-resquest">
                    <p class="truncate tooltipped" data-position="right" data-tooltip="${jobCandidate.user.name}.">${jobCandidate.user.name}</p>
                </div>
                <p class="contact-item-professional center-block dark-color-text">
                    <c:if test="${jobCandidate.user.emailVerified}">
                        <i class="small material-icons green-text tooltipped middle truncate"
                           data-position="top" data-tooltip="Email verificado.">email </i>
                        <strong>
                                ${jobCandidate.user.email}
                        </strong>
                    </c:if>
                    <c:if test="${!jobCandidate.user.emailVerified}">
                        <i class="small material-icons red-text tooltipped middle"
                           data-position="top"
                           data-tooltip="Email não verificado.">email</i>
                        <strong>
                                ${jobCandidate.user.email}
                        </strong>
                    </c:if>
                </p>
                <p class="contact-item-professional center-block dark-color-text">
                    <c:if test="${jobCandidate.user.phoneVerified}">
                        <i class="small material-icons green-text tooltipped middle"
                           data-position="top"
                           data-tooltip="Telefone verificado.">phone </i>
                        <strong>
                                ${jobCandidate.user.phoneNumber}
                        </strong>
                    </c:if>
                    <c:if test="${!jobCandidate.user.phoneVerified}">
                        <i class="small material-icons red-text tooltipped middle"
                           data-position="top"
                           data-tooltip="Telefone não verificado.">phone</i>
                        <strong>
                                ${jobCandidate.user.phoneNumber}
                        </strong>
                    </c:if>
                </p>

            </div>
        </div>
    </div>
</a>
