<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:useBean id="userInfo" type="br.edu.utfpr.servicebook.util.UserTemplateInfo" scope="request"/>
<jsp:useBean id="jobRequest" type="br.edu.utfpr.servicebook.model.dto.JobRequestFullDTO" scope="request"/>
<jsp:useBean id="candidates" type="java.util.List<br.edu.utfpr.servicebook.model.dto.JobCandidateDTO>" scope="request"/>


<head>
    <!-- Funciona apenas com caminho absoluto porque é renderizado antes da tag base -->
    <link href="${pageContext.request.contextPath}/assets/resources/styles/client/client.css" rel="stylesheet">
</head>


<t:template-side-nav title="Detalhes da Solicitação" userInfo="${userInfo}">
    <jsp:body>
        <div class="row">
            <div class="col s12">
                <div class="section">
                    <div class="row">
                        <div class="breadcrumbs" style="margin-top: 20px">
                            <a href="${pageContext.request.contextPath}/">Início</a> &gt;
                            <a href="${pageContext.request.contextPath}/minha-conta/cliente#disponiveis">Minhas
                                Solicitações</a> &gt;
                            Meus Pedidos
                        </div>
                        <!-- Cabeçalho da listagem de candidatos -->
                        <div>
                            <c:if test="${not empty candidates}">
                                <div class="col s12">
                                    <h2 class="secondary-color-text spacing-standard tertiary-color-text">
                                        Escolha um ${expertise.name}!</h2>
                                </div>
                            </c:if>
                            <div class="col s12 m6 tertiary-color-text description-job  text-info-request">
                                <p>${jobRequest.description}</p>
                                <p>Pedido expedido em ${jobRequest.dateTarget}</p>
                            </div>

                            <c:if test="${jobRequest.status == 'AVAILABLE'}">
                                <div class="col s12 m6 l3">
                                    <div class="center">
                                        <a href="#modal-close"
                                           class="spacing-buttons waves-effect waves-light btn modal-trigger">Parar
                                            de receber propostas</a>
                                    </div>
                                </div>
                            </c:if>

                            <c:if test="${jobRequest.status == 'DOING'}">
                                <div class="col s12 m6 l3">
                                    <div class="center">
                                        <a id="closeJobButton" href="#modal-close-job"
                                           class="spacing-buttons waves-effect waves-light btn modal-trigger">PEDIDO
                                            FINALIZADO</a>
                                    </div>
                                </div>

                                <div id="modal-close-job" class="modal">
                                    <div class="modal-content">
                                        <form action="minha-conta/cliente/informa-finalizado/${jobRequest.id}"
                                              method="post">
                                            <input type="hidden" name="_method" value="PATCH"/>

                                            <div class="modal-content" id="modal-content-confirm-hired">
                                                <h5>Finalização do pedido</h5>

                                                <label>
                                                    <input id="confirmCloseJob" name="isQuit" type="radio"
                                                           value="true"/>
                                                    <span>Serviço foi finalizado</span>
                                                </label>

                                                <label>
                                                    <input id="notConfirmCloseJob" name="isQuit"
                                                           type="radio" value="false"/>
                                                    <span>Serviço não foi finalizado</span>
                                                </label>
                                            </div>

                                            <div class="modal-footer">
                                                <button type="button"
                                                        class="modal-close btn-flat waves-effect waves-light btn btn-gray">
                                                    Cancelar
                                                </button>
                                                <button type="submit" disabled
                                                        class="modal-close btn waves-effect waves-light gray"
                                                        id="confirm-job-modal-button">Sim
                                                </button>
                                            </div>
                                        </form>
                                    </div>
                                </div>
                            </c:if>

                            <div class="col s12 m6 l3">
                                <div class="center">
                                    <a
                                            href="#modal-delete"
                                            data-url="${pageContext.request.contextPath}/minha-conta/cliente/desistir/${jobRequest.id}"
                                            class="waves-effect waves-light btn spacing-buttons red modal-trigger"
                                    >Excluir</a
                                    >
                                </div>
                            </div>

                                <%-- HABILITA O CAMPO DE PAGAMENTO APENAS QUANDO O PROFISSIONAL CONFIRMAR --%>
                            <c:if test="${jobRequest.status == 'TO_DO' || jobRequest.status == 'DOING'}">
                                <div class="col s12 m12">
                                    <div class="right-align">
<%--                                        <a href="minha-conta/cliente"--%>
<%--                                           class="spacing-buttons waves-effect waves-light btn">Voltar para--%>
<%--                                            solicitações</a>--%>
<%--                                        <a class="spacing-buttons waves-effect waves-light btn green"--%>
<%--                                           onClick="showPayment(${jobRequest.id})">Pagamento</a>--%>

                                        <input type="hidden" value="${jobRequest.id}" id="jobRequestID">
                                        <input id="mercado-pago-public-key" value="TEST-4f0ff070-df15-44df-9d79-90269d190835" type="hidden" />
                                    </div>

                                    <!-- Payment -->
                                    <section class="payment-form dark">
                                        <div class="container__payment">
                                            <div class="form-payment">
                                                <div class="products">
                                                    <input type="hidden" id="amount" name="transactionAmount" value="${service_professional.price}"/>
                                                    <input type="hidden" id="description" value="${service_professional.description}"/>
                                                    <input type="hidden" id="quantity" value="1" min="1" class="form-control">
                                                </div>
                                                <!-- TODO: Add payment form here -->
                                                <div id="mercadopago-bricks-contaner__PaymentCard"></div>
                                            </div>
                                        </div>
                                    </section>

                                    <div id="statusScreenBrick_container"></div>

                                </div>
                            </c:if>
                        </div>

                        <!-- Subtítulo da listagem -->
                        <div class="col s12 tertiary-color-text description-orcamento text-info-request">
                            <hr class="">
                            <c:if test="${jobRequest.status == 'AVAILABLE'}">
                                <p>Entre em contato com um ou mais profissionais que se interessaram em
                                    realizar o serviço para marcar um orçamento.</p>
                                <c:if test="${candidates.size() > 1}">
                                    <p>${candidates.size()} profissionais responderam a sua
                                        solicitação:</p>
                                </c:if>
                                <c:if test="${candidates.size() == 1}">
                                    <p>${candidates.size()} profissional respondeu a sua
                                        solicitação:</p>
                                </c:if>
                            </c:if>
                            <c:if test="${jobRequest.status == 'BUDGET'}">
                                <p>Solicite e analise o(s) orçamento(s) para escolher o profissional que
                                    melhor atende a sua necessidade.</p>
                            </c:if>
                            <c:if test="${jobRequest.status == 'BUDGET'}">
                                <p>Aguarde a confirmação do profissional para realizar o serviço.</p>
                            </c:if>
                            <c:if test="${jobRequest.status == 'TO_DO'}">
                                <p>O profissional confirmou que realizará o serviço. Você poderá avaliar
                                    o profissional após a expiração da data combinada.</p>
                            </c:if>
                            <c:if test="${jobRequest.status == 'DOING'}">
                                <p>Conforme a data de agendamento, este é o profissional que está
                                    realizando o serviço.
                                    Quando ele finalizar o serviço, informe o término e também faça a
                                    avaliação do profissional.</p>
                            </c:if>
                            <c:if test="${jobRequest.status == 'CLOSED'}">
                                <p>Este é o profissional que realizou o serviço.</p>
                            </c:if>
                        </div>


                        <c:if test="${not empty service_professional}">
                            <div class="col s12 m6 container__cart">
                                <a href="minha-conta/cliente/meus-pedidos/${jobRequest.id}/detalhes/${service_professional.user.id}">
                                    <div class="card-panel card-candidate">
                                        <div class="row no-margin">
                                            <div class="col s12 icons-area-request center">
                                                <div class="row">
                                                    <div class="col s12 center">
                                                        <div class=" candidate dark-color-text">
                                                            <c:forEach var="star" begin="1" end="5">
                                                                <c:if test="${star <= service_professional.user.rating}">
                                                                    <i class="material-icons yellow-text small">star</i>
                                                                </c:if>
                                                                <c:if test="${star > service_professional.user.rating}">
                                                                    <i class="material-icons dark-text small">star_border</i>
                                                                </c:if>
                                                            </c:forEach>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="col s12 center">
                                                <c:if test="${service_professional.user.profilePicture == null}">
                                                    <svg style="width:120px;height:120px"
                                                         viewBox="0 0 24 24">
                                                        <path class="dark-color-icon"
                                                              d="M12,4A4,4 0 0,1 16,8A4,4 0 0,1 12,12A4,4 0 0,1 8,8A4,4 0 0,1 12,4M12,14C16.42,14 20,15.79 20,18V20H4V18C4,15.79 7.58,14 12,14Z"/>
                                                    </svg>
                                                </c:if>
                                                <c:if test="${service_professional.user.profilePicture != null}">
                                                    <div class="row">
                                                        <img src="${service_professional.user.profilePicture}"
                                                             alt="Profissional - Imagem de perfil."
                                                             style="width:110px;height:110px">
                                                    </div>
                                                </c:if>
                                            </div>
                                            <div class="col s12">
                                                <div class="center title-card-resquest">
                                                    <p class="truncate tooltipped" data-position="right"
                                                       data-tooltip="${service_professional.user.name}.">${service_professional.user.name}</p>
                                                </div>
                                                <p class="contact-item-professional center-block dark-color-text">
                                                    <c:if test="${service_professional.user.emailVerified}">
                                                        <i class="small material-icons green-text tooltipped middle truncate"
                                                           data-position="top"
                                                           data-tooltip="Email verificado.">email </i>
                                                        <strong>
                                                                ${service_professional.user.email}
                                                        </strong>
                                                    </c:if>
                                                    <c:if test="${!service_professional.user.emailVerified}">
                                                        <i class="small material-icons red-text tooltipped middle"
                                                           data-position="top"
                                                           data-tooltip="Email não verificado.">email</i>
                                                        <strong>
                                                                ${service_professional.user.email}
                                                        </strong>
                                                    </c:if>
                                                </p>
                                                <p class="contact-item-professional center-block dark-color-text">
                                                    <c:if test="${service_professional.user.phoneVerified}">
                                                        <i class="small material-icons green-text tooltipped middle"
                                                           data-position="top"
                                                           data-tooltip="Telefone verificado.">phone </i>
                                                        <strong>
                                                                ${service_professional.user.phoneNumber}
                                                        </strong>
                                                    </c:if>
                                                    <c:if test="${!service_professional.user.phoneVerified}">
                                                        <i class="small material-icons red-text tooltipped middle"
                                                           data-position="top"
                                                           data-tooltip="Telefone não verificado.">phone</i>
                                                        <strong>
                                                                ${service_professional.user.phoneNumber}
                                                        </strong>
                                                    </c:if>
                                                </p>
                                            </div>
                                        </div>
                                    </div>
                                </a>
                                <button class="btn btn-primary btn-lg btn-block" id="checkout-btn">Realizar Pagamento</button>
                            </div>
                        </c:if>

                        <c:if test="${jobRequest.status == 'CLOSED'}">
                            <c:forEach var="c" items="${jobCandidates12}">
                                <div class="col s12 m6">
                                    <a href="minha-conta/cliente/meus-pedidos/${jobRequest.id}/detalhes/${c.user.id}">
                                        <div class="card-panel card-candidate">
                                            <div class="row no-margin">
                                                <div class="col s12 icons-area-request center">
                                                    <div class="row">
                                                        <div class="col s12 center">
                                                            <div class=" candidate dark-color-text">
                                                                <c:forEach var="star" begin="1" end="5">
                                                                    <c:if test="${star <= c.user.rating}">
                                                                        <i class="material-icons yellow-text small">star</i>
                                                                    </c:if>
                                                                    <c:if test="${star > c.user.rating}">
                                                                        <i class="material-icons dark-text small">star_border</i>
                                                                    </c:if>
                                                                </c:forEach>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                                <div class="col s12 center">
                                                    <c:if test="${c.user.profilePicture == null}">
                                                        <svg style="width:120px;height:120px"
                                                             viewBox="0 0 24 24">
                                                            <path class="dark-color-icon"
                                                                  d="M12,4A4,4 0 0,1 16,8A4,4 0 0,1 12,12A4,4 0 0,1 8,8A4,4 0 0,1 12,4M12,14C16.42,14 20,15.79 20,18V20H4V18C4,15.79 7.58,14 12,14Z"/>
                                                        </svg>
                                                    </c:if>
                                                    <c:if test="${c.user.profilePicture != null}">
                                                        <div class="row">
                                                            <img src="${c.user.profilePicture}"
                                                                 alt="Profissional - Imagem de perfil."
                                                                 style="width:110px;height:110px">
                                                        </div>
                                                    </c:if>
                                                </div>
                                                <div class="col s12">
                                                    <div class="center title-card-resquest">
                                                        <p class="truncate tooltipped" data-position="right"
                                                           data-tooltip="${c.user.name}.">${c.user.name}</p>
                                                    </div>
                                                    <p class="contact-item-professional center-block dark-color-text">
                                                        <c:if test="${c.user.emailVerified}">
                                                            <i class="small material-icons green-text tooltipped middle truncate"
                                                               data-position="top"
                                                               data-tooltip="Email verificado.">email </i>
                                                            <strong>
                                                                    ${c.user.email}
                                                            </strong>
                                                        </c:if>
                                                        <c:if test="${!c.user.emailVerified}">
                                                            <i class="small material-icons red-text tooltipped middle"
                                                               data-position="top"
                                                               data-tooltip="Email não verificado.">email</i>
                                                            <strong>
                                                                    ${c.user.email}
                                                            </strong>
                                                        </c:if>
                                                    </p>
                                                    <p class="contact-item-professional center-block dark-color-text">
                                                        <c:if test="${c.user.phoneVerified}">
                                                            <i class="small material-icons green-text tooltipped middle"
                                                               data-position="top"
                                                               data-tooltip="Telefone verificado.">phone </i>
                                                            <strong>
                                                                    ${c.user.phoneNumber}
                                                            </strong>
                                                        </c:if>
                                                        <c:if test="${!c.user.phoneVerified}">
                                                            <i class="small material-icons red-text tooltipped middle"
                                                               data-position="top"
                                                               data-tooltip="Telefone não verificado.">phone</i>
                                                            <strong>
                                                                    ${c.user.phoneNumber}
                                                            </strong>
                                                        </c:if>
                                                    </p>

                                                </div>
                                            </div>
                                        </div>
                                    </a>

                                    <a href="${pageContext.request.contextPath}/minha-conta/cliente/avaliar/servico/${jobRequest.id}/profissional/${c.user.id}"
                                       class="waves-effect waves-light btn spacing-buttons red modal-trigger"
                                       style="width: 100%">Avaliar Serviço</a>
                                </div>
                            </c:forEach>

                        </c:if>

                        <!-- Listagem de candidatos -->
                        <c:forEach var="c" items="${candidates}">
                            <div class="col s12 l6 ">
                                <t:professional-order-card jobCandidate="${c}"/>

                                <c:if test="${jobRequest.status == 'CLOSED'}">
                                    <a href="${pageContext.request.contextPath}/minha-conta/cliente/avaliar/servico/${jobRequest.id}/profissional/${c.id}"
                                       class="waves-effect waves-light btn spacing-buttons red modal-trigger"
                                       style="width: 100%">Avaliar Serviço</a>
                                </c:if>
                            </div>
                        </c:forEach>
                    </div>
                </div>
            </div>
        </div>
        <!-- Fim conteúdo -->

        <!-- Modal -->
        <div id="modal-close" class="modal">
            <div class="modal-content">
                <form action="${pageContext.request.contextPath}/minha-conta/meus-pedidos/encerra-pedido/${jobRequest.id}"
                      method="post">
                    <input type="hidden" name="_method" value="PATCH"/>
                    <div class="modal-content">
                        <h4>Você tem certeza que deseja encerrar o recebimento de propostas para este pedido? Não
                            será possível reativar depois</h4>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="modal-close btn-flat waves-effect waves-light btn btn-gray">
                            Cancelar
                        </button>
                        <button type="submit" class="modal-close btn waves-effect waves-light gray">Sim</button>
                    </div>
                </form>
            </div>
        </div>
        <div id="modal-delete" class="modal">
            <div class="modal-content">
                <form action="${pageContext.request.contextPath}/minha-conta/cliente/desistir/${jobRequest.id}"
                      method="post">
                    <input type="hidden" name="_method" value="DELETE"/>

                    <div class="modal-content">
                        <h4>
                            Você tem certeza que deseja excluir? Entre em contato com o
                            profissional informando sobre sua decisão!
                        </h4>
                    </div>
                    <div class="modal-footer">
                        <button
                                type="button"
                                class="modal-close btn-flat waves-effect waves-light btn btn-gray"
                        >
                            Cancelar
                        </button>
                        <button
                                type="submit"
                                class="modal-close btn waves-effect waves-light gray"
                        >
                            Sim, excluir!
                        </button>
                    </div>
                </form>
            </div>
        </div>
        <!-- Fim Modal -->
        </main>
    </jsp:body>
</t:template-side-nav>
<style>
    select {
        display: block !important;
    }
</style>

<%--<script>--%>
<%--    const mp = new MercadoPago('TEST-4f0ff070-df15-44df-9d79-90269d190835');--%>
<%--    const bricksBuilder = mp.bricks();--%>

<%--</script>--%>
<%--<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>--%>
<script src="https://sdk.mercadopago.com/js/v2"></script>
<script src="assets/resources/scripts/mp-payment.js"></script>

<script>

    $(document).ready(function () {
        $('.modal').modal({
            onOpenEnd: function (modal, trigger) {
                let url = $(trigger).data('url');
                let name = $(trigger).data('name');

                modal = $(modal);
                let form = modal.find('form');
                form.attr('action', url);
                modal.find('#strong-name').text(name);

                $('#confirmCloseJob').click(function () {
                    $('#confirm-job-modal-button').attr('disabled', false);
                });

                $('#notConfirmCloseJob').click(function () {
                    $('#confirm-job-modal-button').attr('disabled', false);
                });
            }
        });
    });
</script>