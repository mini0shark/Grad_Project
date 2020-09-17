from asgiref.sync import async_to_sync
from channels.generic.websocket import WebsocketConsumer
from userApi.models import User
from chatting.models import Room, Message
from chatting.c_serializer import ChattingListSerializer
from rest_framework.response import Response
import json
from django.db.models import Q

basic_waiting_room_name = 'waiting'
basic_chat_room_name = 'chat'


class WaitingRoom(WebsocketConsumer):
    def connect(self):
        try:  # 방 notice
            sender = self.scope['url_route']['kwargs']['sender']
            opponent = self.scope['url_route']['kwargs']['account_number']
            print("not 방주인")
            self.room_name = "%s_%s_" % (basic_waiting_room_name, opponent)
            self.room_group_name = 'room_%s' % self.room_name
            async_to_sync(self.channel_layer.group_add)(
                self.room_group_name,
                self.channel_name
            )
            self.accept()
            user = User.objects.get(id=opponent)
            try:
                Room.objects.get(name=("%s_%s_" % (basic_waiting_room_name, opponent)))
                print("exist room")
            except:
                Room.objects.create(name=("%s_%s_" % (basic_waiting_room_name, opponent)))
                print("room created")
            pass
            # flag = True
        except:  # 방 주인
            print("방주인")
            userId = self.scope['url_route']['kwargs']['account_number']
            self.room_name = "%s_%s_" % (basic_waiting_room_name, userId)
            self.room_group_name = 'room_%s' % self.room_name
            # Join room group
            async_to_sync(self.channel_layer.group_add)(
                self.room_group_name,
                self.channel_name
            )
            self.accept()
            user = User.objects.get(id=userId)
            print(userId)
            try:
                Room.objects.get(name=("%s_%s_" % (basic_waiting_room_name, userId)))
                print("exist room")
            except:
                Room.objects.create(name=("%s_%s_" % (basic_waiting_room_name, userId)))
                print("room created")
            query = "_%s_" % userId
            rooms = Room.objects.filter(Q(name__icontains=query) & Q(name__icontains="chat"))
            flag = False
            msg_list = None
            for room in rooms:
                room.messages.filter(check=False)
                # msg_list = room.messages.filter(~Q(user=user) & Q(check=False)).order_by('id')
                msg_list = room.messages.filter(~Q(user=user) & Q(check=False)).order_by('id')
                check = msg_list.first()
                print("msglist : ", msg_list)
                if check is not None:
                    flag = True
                    break
            self.send(text_data=json.dumps({
                'flag': flag,
                'messageList': ChattingListSerializer(msg_list, many=True).data
            }))

    def receive(self, text_data):
        text_data_json = json.loads(text_data)
        print(text_data_json)
        try:
            id = text_data_json['id']
        except:
            id = text_data_json['opp']
        async_to_sync(self.channel_layer.group_send)(
            self.room_group_name,
            {'type': 'chat_message',
             'id': id}
        )

    # Receive message from room group
    def chat_message(self, event):
        opp_id = event['id']
        my_id = self.scope['url_route']['kwargs']['account_number']
        user = User.objects.get(id=my_id)
        room = Room.objects.get(name=self.room_name)
        msg = user.messages.filter(user=user, check=False, room=room).order_by('-id')
        query = "_%s_" % my_id
        rooms = Room.objects.filter(Q(name__icontains=query) & Q(name__icontains="chat"))
        msg_list = []
        flag = False
        for room in rooms:
            msg = room.messages.all().order_by('-id').first()  # & Q(check=False)
            if msg is not None:
                msg_list.append(ChattingListSerializer(msg).data)
            msg = room.messages.filter(~Q(user=user) & Q(check=False)).order_by('-id').first()
            if msg is not None:
                flag = True
        print("right?", self.room_name)
        print("right?", msg_list)
        self.send(text_data=json.dumps({
            'flag': flag,
            'messageList': msg_list
        }))


class ChattingRoom(WebsocketConsumer):

    def connect(self):
        self.room_name = self.scope['url_route']['kwargs']['room_name']
        self.room_group_name = '%s_%s_' % (basic_chat_room_name, self.room_name)
        user_id = self.scope['url_route']['kwargs']['account_number']
        # print(self.scope['url_route']['kwargs']['account_number'])
        # Join room group
        try:
            room = Room.objects.get(name=self.room_name)
            user = User.objects.get(id=user_id)
            messages = room.messages.filter(Q(check=False) & ~Q(user=user))
            for msg in messages:
                if msg.user.id != user_id:
                    msg.check = True
                    msg.save()
        except Exception as e:
            print(e)
            try:
                Room.objects.create(name=self.room_name)
            except:
                pass
        async_to_sync(self.channel_layer.group_add)(
            self.room_group_name,
            self.channel_name
        )

        self.accept()

    def disconnect(self, close_code):
        # Leave room group
        user_id = self.scope['url_route']['kwargs']['account_number']
        try:
            room = Room.objects.get(name=self.room_name)
            user = User.objects.get(id=user_id)
            messages = room.messages.filter(Q(check=False) & ~Q(user=user))
            for msg in messages:
                if msg.user.id != user_id:
                    msg.check = True
                    msg.save()
        except Exception as e:
            print(e)
        async_to_sync(self.channel_layer.group_discard)(
            self.room_group_name,
            self.channel_name
        )

    # Receive message from WebSocket
    def receive(self, text_data):
        text_data_json = json.loads(text_data)
        message = text_data_json['message']
        user_id = text_data_json['user']
        isRecipe = text_data_json['isRecipe']
        user = User.objects.get(id=user_id)
        room = Room.objects.get(name=self.room_name)
        Message.objects.create(room=room, type=isRecipe, message=message, user=user)
        # print("roomName : ", self.room_name)
        # Send message to room group
        async_to_sync(self.channel_layer.group_send)(
            self.room_group_name,
            {
                'type': 'chat_message',
                'message': message,
                'user': user_id,
                'isRecipe': isRecipe
            }
        )

    # Receive message from room group
    def chat_message(self, event):
        message = event['message']
        isRecipe = event['isRecipe']
        print("mssage-----1:", message)
        try:
            user = event['user']
        except:
            pass

        # Send message to WebSocket
        self.send(text_data=json.dumps({
            'message': message,
            'user': user,
            'isRecipe': isRecipe
        }))


class ChatConsumer(WebsocketConsumer):
    # websocket 연결 시 실행
    def connect(self):
        self.accept()

    # websocket 연결 종료 시 실행
    def disconnect(self, close_code):
        pass

    # 클라이언트로부터 메세지를 받을 시 실행
    def receive(self, text_data):
        text_data_json = json.loads(text_data)
        message = text_data_json['message']
        # 클라이언트로부터 받은 메세지를 다시 클라이언트로 보내준다.
        self.send(text_data=json.dumps({
            'message': message
        }))
