<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<head>
    <!-- Funciona apenas com caminho absoluto porque é renderizado antes da tag base -->
    <link href="${pageContext.request.contextPath}/assets/resources/styles/professional/professional.css"
          rel="stylesheet">
</head>


<t:template title="Início" userInfo="${userInfo}">
    <jsp:body>
        <main class="container">
            <div class="row">

                <div class="col s12">
                    <h2 class="secondary-color-text">Anúncios de serviços</h2>
                </div>

                <c:if test="${empty jobs}">
                    <t:empty-list/>
                </c:if>

                <div class="row">
                    <c:forEach var="job" items="${jobs}">
                        <a href="minha-conta/profissional/detalhes-servico/${job.id}">
                            <div class="col s12 m6 l4">
                                <div class="card">
                                    <div class="card-title">
                                        <p class="label_especialidade center">${job.expertise.name}</p>
                                    </div>

                                    <div class="card-image">
                                        <c:if test="${empty job.jobImages}">
                                            <div class="black-text text-darken-2 col-image-job-request">
                                                <span class="icone-default"><i
                                                        class="material-icons right">dashboard</i></span>
                                            </div>
                                        </c:if>

                                        <c:if test="${not empty job.jobImages}">
                                            <div class="carousel carousel-slider center">
                                                <c:forEach var="image" items="${job.jobImages}">
                                                    <div class="carousel-item white white-text" href="#${image.id}">
                                                        <img src="${image.path}" class="avatar" style="height: 300px; position: relative;">
                                                        <div class="carousel-fixed-item center">
                                                            <div class="left">
                                                                <a href="Previo" class="movePrevCarousel middle-indicator-text waves-effect waves-light content-indicator">
                                                                    <i class="material-icons left middle-indicator-text">chevron_left</i>
                                                                </a>
                                                            </div>
                                                            <div class="right">
                                                                <a href="Siguiente" class="moveNextCarousel middle-indicator-text waves-effect waves-light content-indicator">
                                                                    <i class="material-icons right middle-indicator-text">chevron_right</i>
                                                                </a>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </c:forEach>
                                            </div>
                                        </c:if>

                                        <span class="card-title"> <i
                                                class="material-icons small blue-color-text icon_default">location_on</i>
                                                ${job.user.address.neighborhood}
                                        </span>
                                    </div>

                                    <div class="card-content">
                                        <p class="description-job-card blue-text"><strong> Solicitado
                                            em: ${job.dateCreated} </strong></p>
                                        <p class="grey-text text-darken-4"> ${job.description} </p>

                                        <p class="description-job-card right grey-text text-darken-4"> ${job.textualDate} </p>
                                    </div>

                                    <div class="card-footer progress" style="position: relative; height: 30px;">
                                        <div class="determinate"
                                             style="width: ${job.totalCandidates / job.quantityCandidatorsMax * 100}%; height: 100%;">
                                        </div>
                                        <div class="progress-text"
                                             style="position: absolute; right: 10px; top: 50%; transform: translateY(-50%); white-space: nowrap;">
                                                ${job.totalCandidates} de ${job.quantityCandidatorsMax}
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </a>
                        <div id="modal-delete" class="modal">
                            <div class="modal-content">
                                <form action="" method="post">
                                    <div class="modal-content">
                                        <h4>
                                            Você tem certeza que deseja não se candidatar para este serviço?
                                            <strong id="strong-name"></strong>?
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
                                            Sim
                                        </button>
                                    </div>
                                </form>
                            </div>
                        </div>

                    </c:forEach>

                </div>

                <div class="container col s12 center-align">
                    <t:pagination-tab-ajax pagination="${pagination}"></t:pagination-tab-ajax>
                </div>
            </div>
        </main>
    </jsp:body>
</t:template>
<style>
    .carousel{
        height: 200px !important;
    }
    .carousel .carousel-fixed-item.center {
        position: absolute;
        top: 50%;
        width: 100%;
        transform: translateY(-50%);
        justify-content: space-between;
    }

    .carousel .carousel-fixed-item .left,
    .carousel .carousel-fixed-item .right {
        z-index: 1;
    }

    .carousel .carousel-fixed-item .left {
        margin-left: 10px;
    }

    .carousel .carousel-fixed-item .right {
        margin-right: 10px;
    }

</style>
<script>
    $(document).ready(function(){
        $('.carousel').carousel();
    });
</script>
<script>
    $(document).ready(function () {
        $(".modal").modal({
            onOpenEnd: function (modal, trigger) {
                var url = $(trigger).data("url");
                var name = $(trigger).data("name");

                modal = $(modal);
                var form = modal.find("form");
                form.attr("action", url);
                modal.find("#strong-name").text(name);
            },
        });
    });
</script>

