from rest_framework import serializers
from chatting.models import Room, Message
from userApi.models import User
from userApi.u_serializers import SimpleUserSerializer


class ChattingListSerializer(serializers.ModelSerializer):
    user_list = serializers.SerializerMethodField()

    def get_user_list(self, obj):
        user = str(obj.room.name).split("_")
        user = user[len(user) - 3:len(user)-1]
        user_list = []
        for _id in user:
            user_id = int(_id)
            user_list.append(SimpleUserSerializer(User.objects.get(id=user_id)).data)
        return user_list

    class Meta:
        model = Message
        fields = ('id', 'user', 'type', 'message', 'check', 'room', 'user_list')


class MessageListSerializer(serializers.ModelSerializer):
    user = SimpleUserSerializer()

    class Meta:
        model = Message
        fields = '__all__'
