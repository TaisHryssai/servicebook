<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:error title="Erro!!">
    <jsp:body>
        <h2 class="center">Ooops!!</h2>
        <p class="center">Acesso negado! </p>

        <div class="row">
            <div class="col s12 center">
                <img class="img-responsive" src="assets/resources/images/401.png" height="500px">
            </div>
        </div>
    </jsp:body>
</t:error>