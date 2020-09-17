from django.urls import path
from . import consumers

websocket_urlpatterns = [
    path("ws/waiting/<account_number>/", consumers.WaitingRoom),
    path("ws/noticeToOpponent/<sender>/<account_number>/", consumers.WaitingRoom),
    path("ws/chatting/<account_number>/<room_name>/", consumers.ChattingRoom),
    path('ws/chatting/<room_name>/', consumers.ChatConsumer),
]
