from django.urls import path
from userApi import views


urlpatterns = [
    # ex /userApi/
    # ex /userApi/
    path('users', views.UsersList.as_view(), name='getUsers'),
    path('user/<user_id>', views.UserDetailAPIView.as_view(), name='userApi'),     ## User 정보
    path('follow/<user_id>', views.push_follow, name='following'),     ## Following
    path('following/<user_id>', views.get_following_list, name='following'),     ## Following
    path('follower/<user_id>', views.get_follower_list, name='follower'),     ## Follower
    path('followingWeeks/<user_id>', views.get_following_weeks_list, name='followingWeeks'),     ## WeeklyFollower
    path('myPage/<pk>', views.get_user_page, name='myPage'),

    path('editUserItem', views.UserDetailAPIView.as_view(), name='editUserInfo'),

    path('searchUser', views.SearchUserList.as_view(), name='searchUser'),

    path('tokenSignIn', views.post_id_token, name='tokenSignIn'),
    path('checkNickName', views.get_check_nickname, name='checkNickName'),
    # path('signup', views.UserSignUp.as_view(), name='signup'),
    # path('login', views.UserLogIn.as_view(), name="login"),
    # path('logout', views.UserLogOut.as_view(), name='logout')
]



## URLconf => 이 파일을 URLconf 라고 생각하면 편함

## path() : route  ''부분 =>
    ## url패턴을 가진 문자열url을 패턴과 리스트의 순서대로 비교함
    ## ex) www.exam.com/myapp의 경우 URLconf는 오직 myapp만 본다

## path의 인수 => view  views.index부분

## path의 인수 => name  views.index부분
    ## URL에 이름을 지으면 템플릿을 포함한  Django어디에서나 명확하게 참조 가능.
