<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html lang="en-us">
<head>
    <jsp:include page="/WEB-INF/jsp/layout/head.jsp" />
</head>
<body id="login">
<!-- possible classes: minified, no-right-panel, fixed-ribbon, fixed-header, fixed-width-->
<header id="header">
    <!--<span id="logo"></span>-->

    <div id="logo-group">
        <span id="logo"> <img src="img/logo.png" alt="SmartAdmin"> </span>

        <!-- END AJAX-DROPDOWN -->
    </div>

    <span id="login-header-space"> <span class="hidden-mobile">Already registered?</span> <a href="login.html" class="btn btn-danger">Sign In</a> </span>

</header>

<div id="main" role="main">

    <!-- MAIN CONTENT -->
    <div id="content" class="container">

        <div class="row">
            <div class="col-xs-12 col-sm-12 col-md-7 col-lg-7 hidden-xs hidden-sm">
                <h1 class="txt-color-red login-header-big">好记·记账本</h1>
                <div class="hero">

                    <div class="pull-left login-desc-box-l">
                        <h4 class="paragraph-header">It's Okay to be Smart. Experience the simplicity of SmartAdmin, everywhere you go!</h4>
                        <div class="login-app-icons">
                            <a href="javascript:void(0);" class="btn btn-danger btn-sm">Frontend Template</a>
                            <a href="javascript:void(0);" class="btn btn-danger btn-sm">Find out more</a>
                        </div>
                    </div>

                    <img src="img/demo/iphoneview.png" alt="" class="pull-right display-image" style="width:210px">

                </div>

                <div class="row">
                    <div class="col-xs-12 col-sm-12 col-md-6 col-lg-6">
                        <h5 class="about-heading">About SmartAdmin - Are you up to date?</h5>
                        <p>
                            Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa.
                        </p>
                    </div>
                    <div class="col-xs-12 col-sm-12 col-md-6 col-lg-6">
                        <h5 class="about-heading">Not just your average template!</h5>
                        <p>
                            Et harum quidem rerum facilis est et expedita distinctio. Nam libero tempore, cum soluta nobis est eligendi voluptatem accusantium!
                        </p>
                    </div>
                </div>

            </div>
            <div class="col-xs-12 col-sm-12 col-md-5 col-lg-5">
                <div class="well no-padding">

                    <form action="reg.htm" method="post" id="smart-form-register" class="smart-form client-form">
                        <header>
                                免费注册 FREE*
                        </header>

                        <fieldset>
                            <section>
                                <label class="input"> <i class="icon-append fa fa-user"></i>
                                    <input type="text" name="username" placeholder="用户名">
                                    <b class="tooltip tooltip-bottom-right">用于登录网站</b> </label>
                            </section>

                            <section>
                                <label class="input"> <i class="icon-append fa fa-envelope"></i>
                                    <input type="email" name="email" placeholder="邮件地址">
                                    <b class="tooltip tooltip-bottom-right">用于验证您的账户</b> </label>
                            </section>

                            <section>
                                <label class="input"> <i class="icon-append fa fa-lock"></i>
                                    <input type="password" name="password" placeholder="密码" id="password">
                                    <b class="tooltip tooltip-bottom-right">一定记得您的密码哟</b> </label>
                            </section>

                            <section>
                                <label class="input"> <i class="icon-append fa fa-lock"></i>
                                    <input type="password" name="passwordConfirm" placeholder="确认密码">
                                    <b class="tooltip tooltip-bottom-right">请与您的密码保持一致</b> </label>
                            </section>
                        </fieldset>

                        <fieldset>
                            <div class="row">
                                <section class="col col-6">
                                    <label class="input">
                                        <input type="text" name="lastname" placeholder="姓">
                                    </label>
                                </section>
                                <section class="col col-6">
                                    <label class="input">
                                        <input type="text" name="firstname" placeholder="名">
                                    </label>
                                </section>
                            </div>

                            <div class="row">
                                <section class="col col-6">
                                    <label class="select">
                                        <select name="gender">
                                            <option value="0" selected="" disabled="">性别</option>
                                            <option value="1">男</option>
                                            <option value="2">女</option>
                                        </select> <i></i> </label>
                                </section>
                            </div>

                            <section>
                                <label class="checkbox">
                                    <input type="checkbox" name="subscription" id="subscription">
                                    <i></i>我希望收到“好记”的新闻和教程</label>
                                <label class="checkbox">
                                    <input type="checkbox" name="terms" id="terms">
                                    <i></i>我同意 <a href="#" data-toggle="modal" data-target="#myModal"> 服务协议 </a></label>
                            </section>
                        </fieldset>
                        <footer>
                            <button type="submit" class="btn btn-primary">
                                注册
                            </button>
                        </footer>

                        <div class="message">
                            <i class="fa fa-check"></i>
                            <p>
                                感谢您的注册
                            </p>
                        </div>
                    </form>

                </div>
                <p class="note text-center">*FREE Registration ends on October 2025.</p>
                <h5 class="text-center">- Or sign in using -</h5>
               <%-- <ul class="list-inline text-center">
                    <li>
                        <a href="javascript:void(0);" class="btn btn-primary btn-circle"><i class="fa fa-facebook"></i></a>
                    </li>
                    <li>
                        <a href="javascript:void(0);" class="btn btn-info btn-circle"><i class="fa fa-twitter"></i></a>
                    </li>
                    <li>
                        <a href="javascript:void(0);" class="btn btn-warning btn-circle"><i class="fa fa-linkedin"></i></a>
                    </li>
                </ul>--%>
            </div>
        </div>
    </div>

</div>

<!-- Modal -->
<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                    &times;
                </button>
                <h4 class="modal-title" id="myModalLabel">Terms & Conditions</h4>
            </div>
            <div class="modal-body custom-scroll terms-body">

                <div id="left">



                    <h1>好记·记账本使用协议</h1>



                    <h2>不准瞎搞</h2>


            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Cancel
                </button>
                <button type="button" class="btn btn-primary" id="i-agree">
                    <i class="fa fa-check"></i> I Agree
                </button>

                <button type="button" class="btn btn-danger pull-left" id="print">
                    <i class="fa fa-print"></i> Print
                </button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->

<!--================================================== -->

<!-- PACE LOADER - turn this on if you want ajax loading to show (caution: uses lots of memory on iDevices)
<script data-pace-options='{ "restartOnRequestAfter": true }' src="js/plugin/pace/pace.min.js"></script>-->

<!-- Link to Google CDN's jQuery + jQueryUI; fall back to local -->
<script src="http://cdn.bootcss.com/jquery/2.0.2/jquery.min.js"></script>
<script> if (!window.jQuery) { document.write('<script src="js/libs/jquery-2.0.2.min.js"><\/script>');} </script>

<script src="//cdn.bootcss.com/jqueryui/1.10.3/jquery-ui.min.js"></script>
<script> if (!window.jQuery.ui) { document.write('<script src="js/libs/jquery-ui-1.10.3.min.js"><\/script>');} </script>

<!-- JS TOUCH : include this plugin for mobile drag / drop touch events
<script src="js/plugin/jquery-touch/jquery.ui.touch-punch.min.js"></script> -->

<!-- BOOTSTRAP JS -->
<script src="js/bootstrap/bootstrap.min.js"></script>

<!-- CUSTOM NOTIFICATION -->
<script src="js/notification/SmartNotification.min.js"></script>

<!-- JARVIS WIDGETS -->
<script src="js/smartwidgets/jarvis.widget.min.js"></script>

<!-- EASY PIE CHARTS -->
<script src="js/plugin/easy-pie-chart/jquery.easy-pie-chart.min.js"></script>

<!-- SPARKLINES -->
<script src="js/plugin/sparkline/jquery.sparkline.min.js"></script>

<!-- JQUERY VALIDATE -->
<script src="js/plugin/jquery-validate/jquery.validate.min.js"></script>

<!-- JQUERY MASKED INPUT -->
<script src="js/plugin/masked-input/jquery.maskedinput.min.js"></script>

<!-- JQUERY SELECT2 INPUT -->
<script src="js/plugin/select2/select2.min.js"></script>

<!-- JQUERY UI + Bootstrap Slider -->
<script src="js/plugin/bootstrap-slider/bootstrap-slider.min.js"></script>

<!-- browser msie issue fix -->
<script src="js/plugin/msie-fix/jquery.mb.browser.min.js"></script>

<!-- FastClick: For mobile devices -->
<script src="js/plugin/fastclick/fastclick.js"></script>

<!--[if IE 7]>

<h1>Your browser is out of date, please update your browser by going to www.microsoft.com/download</h1>

<![endif]-->

<!-- MAIN APP JS FILE -->
<script src="js/app.js"></script>

<script type="text/javascript">
    runAllForms();

    // Model i agree button
    $("#i-agree").click(function(){
        $this=$("#terms");
        if($this.checked) {
            $('#myModal').modal('toggle');
        } else {
            $this.prop('checked', true);
            $('#myModal').modal('toggle');
        }
    });

    // Validation
    $(function() {
        // Validation
        $("#smart-form-register").validate({

            // Rules for form validation
            rules : {
                username : {
                    required : true
                },
                email : {
                    required : true,
                    email : true
                },
                password : {
                    required : true,
                    minlength : 3,
                    maxlength : 20
                },
                passwordConfirm : {
                    required : true,
                    minlength : 3,
                    maxlength : 20,
                    equalTo : '#password'
                },
                firstname : {
                    required : true
                },
                lastname : {
                    required : true
                },
                gender : {
                    required : true
                },
                terms : {
                    required : true
                }
            },

            // Messages for form validation
            messages : {
                login : {
                    required : 'Please enter your login'
                },
                email : {
                    required : 'Please enter your email address',
                    email : 'Please enter a VALID email address'
                },
                password : {
                    required : 'Please enter your password'
                },
                passwordConfirm : {
                    required : 'Please enter your password one more time',
                    equalTo : 'Please enter the same password as above'
                },
                firstname : {
                    required : 'Please select your first name'
                },
                lastname : {
                    required : 'Please select your last name'
                },
                gender : {
                    required : 'Please select your gender'
                },
                terms : {
                    required : 'You must agree with Terms and Conditions'
                }
            },

            // Ajax form submition
            submitHandler : function(form) {
                $(form).ajaxSubmit({
                    success : function() {
                        $("#smart-form-register").addClass('submited');
                    }
                });
            },

            // Do not change code below
            errorPlacement : function(error, element) {
                error.insertAfter(element.parent());
            }
        });

    });
</script>
</body>
</html>
