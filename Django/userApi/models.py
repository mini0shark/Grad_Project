import datetime

from django.contrib.auth.models import AbstractBaseUser
from django.db import models
from django.utils import timezone


# =================== 수정 후 실행================ #
# python manage.py makemigrations users
# python manage.py sqlmigrate cooks 0001
# python manage.py migrate


def profile_image_path(instance, filename):
    # file will be uploaded to MEDIA_ROOT/user_<id>/<filename>
    return 'user_{0}/{1}'.format(instance.id, filename)


class User(AbstractBaseUser):
    user_id = models.CharField(unique=True, max_length=100, null=True, blank=True)
    name = models.CharField(max_length=100)
    introduce = models.TextField(null=True, blank=True)
    join_date = models.DateTimeField(auto_now_add=True, null=True, blank=True)
    favorite_food = models.CharField(max_length=200, null=True)
    nickname = models.CharField(max_length=100, null=True)
    member_type = models.IntegerField(default=0, null=True, blank=True)     # 0 멤버X 1 = 구글, 2 일반
    profile_image = models.ImageField(upload_to=profile_image_path, null=True, blank=True)
    followers = models.ManyToManyField("self", blank=True, symmetrical=False, through='FollowingRelation')
    USERNAME_FIELD = 'user_id'

    def __str__(self):
        return "User" + str(self.pk) + " : " + str(self.user_id) + " : " + str(self.name)+"  \n nick:  " +str(self.nickname)+"  :  "+"  \n fav:  " +str(self.favorite_food)

    def was_published_recently(self):
        now = timezone.now()
        return now >= self.join_date >= now - datetime.timedelta(days=1)

    def total_followers(self):
        return self.follower.count()


class FollowingRelation(models.Model):

    class Meta:
        unique_together = ('follower', 'following')

    follower = models.ForeignKey(User, on_delete=models.CASCADE,  related_name="relation_follower")  # follower랑 following 이랑 바꿔서 하면 됨
    following = models.ForeignKey(User, on_delete=models.CASCADE,  related_name="relation_following")
    created_time = models.DateTimeField(auto_now_add=True, null=True, blank=True)
