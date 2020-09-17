from django.shortcuts import render
from django.utils.safestring import mark_safe
from rest_framework.response import Response
from rest_framework.decorators import api_view
from chatting.models import Message
from chatting.c_serializer import ChattingListSerializer, MessageListSerializer
from chatting.models import Room, Message
from userApi.models import User
from userApi.u_serializers import SimpleUserSerializer
from django.db.models import Q
import json


def index(request):
    return render(request, 'chatting/index.html', {})


def room(request, room_name):
    return render(request, 'chatting/room.html', {
        'room_name_json': mark_safe(json.dumps(room_name))
    })


@api_view(['get'])
def chatting_list(request):
    user_id = request.GET['user_id']
    query = "_%s_" % user_id
    print("req : ", query)
    rooms = Room.objects.filter(Q(name__icontains=query) & Q(name__icontains="chat"))
    print(rooms)
    msg_list = []
    for r in rooms:
        msg_list.append(ChattingListSerializer(r.messages.all().order_by('-id').first()).data)
    print(msg_list)
    return Response(msg_list)


@api_view(['get'])
def chatting_before(request):
    sender = request.GET['sender']
    receiver = request.GET['receiver']
    if int(sender) > int(receiver):
        room_name = "%s_%s_" % (receiver, sender)
    else:
        room_name = "%s_%s_" % (sender, receiver)
    chat_room_name = "chat_" + room_name
    print(chat_room_name)
    try:
        room = Room.objects.get(name=chat_room_name)
        message_list = room.messages
        msg_list = MessageListSerializer(message_list, many=True).data
        result = {"msg_list": msg_list}
    except Exception as e:
        print(e)
        result = {"msg_list": None}
    opponent = User.objects.get(id=sender)
    opp_ser = SimpleUserSerializer(opponent).data
    result['user'] = opp_ser
    return Response(result)
    # msg = Message.objects.all()
    # ser = ChattingListSerializer(msg, many=True)
    # print(request.GET)


@api_view(['get'])
def check_is_new(request, user_id, message_id):
    m = Message.objects.get(id=message_id)
    user = User.objects.get(id=user_id)
    print(m.user)
    print(user)
    if m.user == user:
        return Response(False)
    else:
        return Response(True)
