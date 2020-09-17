from rest_framework import serializers
from userApi.models import User, FollowingRelation


class UserSerializer(serializers.ModelSerializer):
    # recipe_container = RecipeContainerSerializer(many=True)
    # comment = CommentSerializer(many=True)

    class Meta:
        model = User
        fields = ('id', 'password', 'last_login','user_id','name',
                  'introduce', 'join_date', 'favorite_food', 'nickname',
                  'member_type', 'profile_image', 'followers')
        depth = 1


class SimpleUserSerializer(serializers.ModelSerializer):
    # recipe_container = RecipeContainerSerializer(many=True)
    # comment = CommentSerializer(many=True)

    class Meta:
        model = User
        fields = ('id', 'user_id', 'nickname', 'profile_image')


class SearchAccountSerializer(serializers.ModelSerializer):
    # recipe_container = RecipeContainerSerializer(many=True)
    # comment = CommentSerializer(many=True)
    post_count = serializers.SerializerMethodField()
    follower_count = serializers.SerializerMethodField()

    def get_post_count(self, obj):
        return obj.recipe_container.count()

    def get_follower_count(self, obj):
        return obj.relation_follower.count()

    class Meta:
        model = User
        fields = ('id', 'user_id', 'nickname', 'profile_image', 'post_count', 'follower_count')


class MyPageUserSerializer(serializers.ModelSerializer):
    # recipe_container = RecipeContainerSerializer(many=True)
    # comment = CommentSerializer(many=True)
    favorite_food = serializers.SerializerMethodField()
    follower_count = serializers.SerializerMethodField()
    following_count = serializers.SerializerMethodField()
    recipe_count = serializers.SerializerMethodField()
    story_count = serializers.SerializerMethodField()

    def get_story_count(self, obj):
        str = obj.recipe_container.filter(type=False).count()
        return str

    def get_recipe_count(self, obj):
        str = obj.recipe_container.filter(type=True).count()
        return str

    def get_following_count(self, obj):
        str = obj.relation_following.count()
        return str

    def get_follower_count(self, obj):
        str = obj.relation_follower.count()
        return str

    def get_favorite_food(self, obj):
        try:
            str = obj.favorite_food.split(',')
            return str
        except:
            return None

    class Meta:
        model = User
        fields = ('id', 'nickname', 'profile_image', 'favorite_food', 'introduce', 'recipe_count', 'story_count',  'follower_count', 'following_count')


class FollowingSerializer(serializers.ModelSerializer):
    id = serializers.SerializerMethodField()
    nickname = serializers.SerializerMethodField()
    profile_image = serializers.SerializerMethodField()

    def get_id(self, obj):
        return obj.following.id

    def get_nickname(self, obj):
        return obj.following.nickname

    def get_profile_image(self, obj):
        print()
        try:
            return obj.following.profile_image.url
        except :
            return None

    class Meta:
        model = FollowingRelation
        fields = ('id', 'nickname', 'profile_image', 'created_time')


class FollowerSerializer(serializers.ModelSerializer):
    id = serializers.SerializerMethodField()
    nickname = serializers.SerializerMethodField()
    profile_image = serializers.SerializerMethodField()

    def get_id(self, obj):
        return obj.follower.id

    def get_nickname(self, obj):
        return obj.follower.nickname

    def get_profile_image(self, obj):
        print(obj.follower)
        try:
            return obj.follower.profile_image.url
        except :
            return None

    class Meta:
        model = FollowingRelation
        fields = ('id', 'nickname', 'profile_image', 'created_time')


class SearchVerySimpleUserSerializer(serializers.ModelSerializer):
    # recipe_container = RecipeContainerSerializer(many=True)
    # comment = CommentSerializer(many=True)

    class Meta:
        model = User
        fields = ('id', 'nickname')

