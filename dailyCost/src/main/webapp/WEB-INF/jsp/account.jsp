<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: Lucas
  Date: 2017-03-03
  Time: 15:09
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <jsp:include page="layout/head.jsp"/>
    <title>我的账户</title>

    <style>
        .dc_category li {
            width: 10px;
            height: 10px;
            float: left;
            padding-left: 4px;
        }
    </style>
</head>
<body>
<jsp:include page="layout/header.jsp"/>
<jsp:include page="layout/left.jsp"/>

<div id="main" role="main">
    <jsp:include page="layout/main_ribbon.jsp">
        <jsp:param name="breadcrumb" value="我的账户"/>
    </jsp:include>
    <!-- MAIN CONTENT -->
    <div id="content">
        <div class="jarviswidget jarviswidget-sortable" id="wid-id-0" data-widget-colorbutton="false"
             data-widget-editbutton="false" role="widget" style="">
            <!-- widget options:
            usage: <div class="jarviswidget" id="wid-id-0" data-widget-editbutton="false">

            data-widget-colorbutton="false"
            data-widget-editbutton="false"
            data-widget-togglebutton="false"
            data-widget-deletebutton="false"
            data-widget-fullscreenbutton="false"
            data-widget-custombutton="false"
            data-widget-collapsed="true"
            data-widget-sortable="false"

            -->
            <header role="heading">
                <div class="jarviswidget-ctrls" role="menu"><a href="#" class="button-icon jarviswidget-toggle-btn"
                                                               rel="tooltip" title="" data-placement="bottom"
                                                               data-original-title="Collapse"><i
                        class="fa fa-minus "></i></a> <a href="javascript:void(0);"
                                                         class="button-icon jarviswidget-fullscreen-btn" rel="tooltip"
                                                         title="" data-placement="bottom"
                                                         data-original-title="Fullscreen"><i
                        class="fa fa-resize-full "></i></a> <a href="javascript:void(0);"
                                                               class="button-icon jarviswidget-delete-btn" rel="tooltip"
                                                               title="" data-placement="bottom"
                                                               data-original-title="Delete"><i class="fa fa-times"></i></a>
                </div>
                <span class="widget-icon"> <i class="fa fa-eye"></i> </span>
                <h2>Default Elements</h2>

                <span class="jarviswidget-loader"><i class="fa fa-refresh fa-spin"></i></span></header>

            <!-- widget div-->
            <div role="content">

                <!-- widget edit box -->
                <div class="jarviswidget-editbox">
                    <!-- This area used as dropdown edit box -->

                </div>
                <!-- end widget edit box -->

                <!-- widget content -->
                <div class="widget-body">

                    <form class="form-horizontal">

                        <fieldset>
                            <legend>开始记账吧</legend>

                            <div class="form-group">
                                <label class="col-md-2 control-label">金额</label>
                                <div class="col-md-3">
                                    <input class="form-control " name="amount" id="spinner-decimal" name="spinner-decimal"
                                           value="0.00">
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-2 control-label" for="multiselect1">类别</label>
                                <div class="col-md-3">
                                    <select multiple="multiple" id="multiselect1" name="category" class="form-control custom-scroll"
                                            title="选择您的花费分类">
                                    <c:forEach items="categoryList" var="c">
                                        <option value="${c.code}">${c.message}</option>
                                    </c:forEach>
                                    </select>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-2 control-label">备注</label>
                                <div class="col-md-3">
                                    <textarea class="form-control" name="memo" placeholder="备注" rows="4"></textarea>
                                </div>
                            </div>

                        </fieldset>

                        <div class="form-actions">
                            <div class="row">
                                <div class="col-md-5">
                                    <button class="btn btn-default" type="submit">
                                        Cancel
                                    </button>
                                    <button class="btn btn-primary" type="submit">
                                        <i class="fa fa-save"></i>
                                        Submit
                                    </button>
                                </div>
                            </div>
                        </div>

                    </form>

                </div>
                <!-- end widget content -->

            </div>
            <!-- end widget div -->

        </div>


    </div>
    <!-- END MAIN CONTENT -->

    <jsp:include page="layout/footer.jsp"/>


    <!-- Demo purpose only -->
    <script src="js/demo.js"></script>

    <!-- MAIN APP JS FILE -->
    <script src="js/app.js"></script>

    <!-- PAGE RELATED PLUGIN(S)
    <script src="..."></script>-->
    <script src="js/plugin/maxlength/bootstrap-maxlength.min.js"></script>
    <script src="js/plugin/bootstrap-timepicker/bootstrap-timepicker.min.js"></script>
    <script src="js/plugin/bootstrap-tags/bootstrap-tagsinput.min.js"></script>
    <script src="js/plugin/noUiSlider/jquery.nouislider.min.js"></script>
    <script src="js/plugin/ion-slider/ion.rangeSlider.min.js"></script>
    <script src="js/plugin/colorpicker/bootstrap-colorpicker.min.js"></script>
    <script src="js/plugin/knob/jquery.knob.min.js"></script>
    <script src="js/plugin/x-editable/moment.min.js"></script>
    <script src="js/plugin/x-editable/jquery.mockjax.min.js"></script>
    <script src="js/plugin/x-editable/x-editable.min.js"></script>
    <script src="js/plugin/typeahead/typeahead.min.js"></script>
    <script src="js/plugin/typeahead/typeaheadjs.min.js"></script>


    <script type="text/javascript">

        // DO NOT REMOVE : GLOBAL FUNCTIONS!

        $(document).ready(function () {

            pageSetUp();

            $("#spinner").spinner();
            $("#spinner-decimal").spinner({
                step: 0.01,
                numberFormat: "n"
            });

            $("#spinner-currency").spinner({
                min: 5,
                max: 2500,
                step: 25,
                start: 1000,
                numberFormat: "C"
            });


        })

    </script>

</div>
</body>
</html>
