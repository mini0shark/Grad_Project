from __future__ import unicode_literals
from django.db import models
from django.utils import timezone
from django.utils.text import slugify
from userApi.models import User
from recipeApi.models import RecipeContainer


class Room(models.Model):
    name = models.CharField(max_length=200, unique=True, null=True, blank=True)
    recipe = models.ForeignKey(RecipeContainer, related_name='room', on_delete=models.CASCADE, null=True)


class Message(models.Model):
    room = models.ForeignKey(Room, related_name='messages', on_delete=models.CASCADE)
    type = models.IntegerField(default=0)            ## 참조메세지(레시피 번호)1 vs 일반0  =
    message = models.TextField(null=True, blank=True)
    user = models.ForeignKey(User, related_name="messages", on_delete=models.DO_NOTHING)
    check = models.BooleanField(default=False)
