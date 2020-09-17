from django.urls import path
from chatting import views

urlpatterns = [
    path('', views.index, name='index'),
    path('<str:room_name>/', views.room, name='room'),
    path('getChattingBefore', views.chatting_before, name='getChattingBefore'),
    path('getChattingList', views.chatting_list, name='getChattingList'),
    path('getIsNew/<user_id>/<message_id>', views.check_is_new, name='getIsNew'),

]
