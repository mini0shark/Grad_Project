from rest_framework import serializers
from recipeApi.models import RecipeContainer, Recipe, MultiImageResult, Comment, Ingredients, ExtraTip, \
    RecipeLikeRelation, CommentLikeRelation, History, ReferencesRelation, RecipeWishListRelation
from userApi.u_serializers import SimpleUserSerializer, User,  FollowingRelation, SearchVerySimpleUserSerializer


class OnlyUserRecipeLikeIntermediary(serializers.ModelSerializer):

    class Meta:
        model = RecipeLikeRelation
        fields = ('user',)


class OnlyUserRecipeListIntermediary(serializers.ModelSerializer):

    class Meta:
        model = RecipeWishListRelation
        fields = ('user',)


class RecipeLikeRelationSerializer(serializers.ModelSerializer):

    class Meta:
        model = RecipeLikeRelation
        fields = ('from_user', 'created_time')


class FollowRelationSerializer(serializers.ModelSerializer):
    follower = SimpleUserSerializer()

    class Meta:
        model = FollowingRelation
        fields = ('follower', 'following', 'created_time')


class CommentLikeRelationSerializer(serializers.ModelSerializer):
    from_user = SimpleUserSerializer()

    class Meta:
        model = CommentLikeRelation
        fields = ('to_comment', 'from_user', 'created_time')


class RecipeContainerIdSerializer(serializers.ModelSerializer):
    user = SimpleUserSerializer()

    class Meta:
        model = RecipeContainer
        fields = ('id', 'recipe_title', 'food_name', 'user')


class ReferenceRelationSerializer(serializers.ModelSerializer):
    recipe_original = RecipeContainerIdSerializer()
    recipe_copied = RecipeContainerIdSerializer()

    class Meta:
        model = ReferencesRelation
        fields = ('id', 'recipe_original', 'recipe_copied')


# ################################################################### 위쪽 => 중계


class RecipeSerializer(serializers.ModelSerializer):

    class Meta:
        model = Recipe
        fields = '__all__'


class MultiImageResultSerializer(serializers.ModelSerializer):

    class Meta:
        model = MultiImageResult
        fields = '__all__'


class CommentSerializer(serializers.ModelSerializer):
    user = SimpleUserSerializer()

    class Meta:
        model = Comment
        fields = '__all__'


class ExtraTipSerializer(serializers.ModelSerializer):
    class Meta:
        model = ExtraTip
        fields = '__all__'


class IngredientsSerializer(serializers.ModelSerializer):
    class Meta:
        model = Ingredients
        fields = '__all__'


class HomePageSerializer(serializers.ModelSerializer):      # 홈페이지 아이템
    comment_count = serializers.SerializerMethodField()
    likes = serializers.SerializerMethodField()
    # list = IdOnlyUserSerializer()
    multi_image_result = MultiImageResultSerializer(many=True)
    user = SimpleUserSerializer()
    list = serializers.SerializerMethodField()

    def get_list(self, obj):
        list_obj = obj.recipe_wish_list_recipe.all()
        ser = OnlyUserRecipeListIntermediary(list_obj, many=True)
        return ser.data

    def get_comment_count(self, obj):
        return obj.comment_container.count()

    def get_likes(self, obj):
        likes = obj.recipe_like_recipe.all()
        ser = OnlyUserRecipeLikeIntermediary(likes, many=True)
        return ser.data

    def get_multi_image_result(self, obj):
        rep_image = MultiImageResult.objects.filter(recipe_container_id=obj.id).order_by('id').first().image.url
        return rep_image
        for img in rep_image:
            return img.image.url

    class Meta:
        model = RecipeContainer
        fields = ('id', 'multi_image_result', 'comment_count', 'likes', 'recipe_title',
                  'introduce_recipe', 'food_name', 'category', 'for_person', 'required_time',
                  'created_date', 'type', 'user', 'list', 'hit_count',)


class RecipeDetailSerializer(serializers.ModelSerializer):       # RecipeDetail 용
    recipe_order = RecipeSerializer(many=True)
    multi_image_result = MultiImageResultSerializer(many=True)
    extra_tip = ExtraTipSerializer(many=True)
    comment_container = CommentSerializer(many=True)
    ingredients = IngredientsSerializer(many=True)
    likes = serializers.SerializerMethodField()
    user = SimpleUserSerializer()
    list = serializers.SerializerMethodField()


    def get_list(self, obj):
        list_obj = obj.recipe_wish_list_recipe.all()
        ser = OnlyUserRecipeListIntermediary(list_obj, many=True)
        return ser.data

    def get_likes(self, obj):
        likes = obj.recipe_like_recipe.all()
        ser = OnlyUserRecipeLikeIntermediary(likes, many=True)
        return ser.data

    class Meta:
        model = RecipeContainer
        fields = '__all__'
        depth = 1


class StoryDetailSerializer(serializers.ModelSerializer):       # RecipeDetail 용
    multi_image_result = MultiImageResultSerializer(many=True)
    comment_container = CommentSerializer(many=True)
    likes = serializers.SerializerMethodField()
    user = SimpleUserSerializer()
    recipe_copied = ReferenceRelationSerializer(many=True)

    def get_likes(self, obj):
        likes = obj.recipe_like_recipe.all()
        ser = OnlyUserRecipeLikeIntermediary(likes, many=True)
        return ser.data

    class Meta:
        model = RecipeContainer
        fields = ('id', 'user', 'introduce_recipe', 'multi_image_result', 'created_date',
                  'type', 'recipe_copied', 'likes', 'hit_count', 'comment_container')
        depth = 1


class RecipeContainerForHistorySerializer(serializers.ModelSerializer):      # 홈페이지 아이템
    comment_count = serializers.SerializerMethodField()
    multi_image_result = MultiImageResultSerializer(many=True)

    def get_comment_count(self, obj):
        return obj.comment_container.count()

    def get_multi_image_result(self, obj):
        rep_image = MultiImageResult.objects.filter(recipe_container_id=obj.id).order_by('id').first()
        try:
            return rep_image.image.url
        except:
            return None

    class Meta:
        model = RecipeContainer
        fields = ('id', 'multi_image_result', 'comment_count', 'recipe_title')


class HistorySerializer(serializers.ModelSerializer):
    object = serializers.SerializerMethodField()
    count = serializers.SerializerMethodField()
    content = serializers.SerializerMethodField()
    user = serializers.SerializerMethodField()

    def get_user(self, obj):
        classification = obj.classify
        if classification == 'RL':
            try:
                model = RecipeContainer.objects.get(id=obj.model_id)
                like_present = model.recipe_like_recipe.all().first().user
                return SimpleUserSerializer(like_present).data
            except Exception as e:
                print("1", e)
                return None
        elif classification == 'RC':
            try:
                model = RecipeContainer.objects.get(id=obj.model_id)
                comment_present = model.comment_container.all().first()
                comment_present = comment_present.user
                return SimpleUserSerializer(comment_present).data
            except Exception as e:
                print("2", e)
                return None
        elif classification == 'RR':
            try:
                model = RecipeContainer.objects.get(id=obj.model_id)
                relation = model.reference.all().order_by('-id').first()
                represent_user = relation.user
                return SimpleUserSerializer(represent_user).data
            except Exception as e:
                print("3", e)
                return None
        elif classification == 'UF':
            try:
                model = User.objects.get(id=obj.model_id)
                return SimpleUserSerializer(model).data
            except Exception as e:
                return None
        elif classification == 'CL':
            try:
                model = Comment.objects.get(id=obj.model_id)
                relation = model.likes.all().order_by('-id').first()
                represent_user = relation
                return SimpleUserSerializer(represent_user).data
            except Exception as e:
                print("4", e)
                return None
        return None

    def get_object(self, obj):
        classification = obj.classify
        model_id = obj.model_id
        if classification == 'RL':
            try:
                recipe = RecipeContainer.objects.get(id=model_id)
                return VerySimpleRecipeContainerSerializer(recipe).data
            except Exception as e:
                print("5", e)
                return None
        elif classification == 'RC':
            try:
                recipe = RecipeContainer.objects.get(id=model_id)
                return VerySimpleRecipeContainerSerializer(recipe).data
            except Exception as e:
                print("6", e)
                return None
        elif classification == 'RR':
            try:
                recipe = RecipeContainer.objects.get(id=model_id)
                hist = VerySimpleRecipeContainerSerializer(recipe).data
                print(hist)
                return hist
            except Exception as e:
                print("hist - ", e)
                return None
        elif classification == 'UF':
            return None
        elif classification == 'CL':
            try:
                comment = Comment.objects.get(id=model_id)
                ser = HistoryForReferenceRecipeContainerSerializer(comment.recipe_container).data
                return ser
            except Exception as e:
                print("8", e)
                return None
        return None

    def get_count(self, obj):
        classification = obj.classify
        model_id = obj.model_id
        if classification == 'RL':
            try:
                return RecipeContainer.objects.get(id=model_id).likes.count()
            except Exception as e:
                print(e)
                return -1
        elif classification == 'RC':
            try:
                recipe = RecipeContainer.objects.get(id=model_id).comment_container.count()
                return recipe
            except Exception as e:
                print(e)
                return -1
        elif classification == 'RR':
            try:
                recipe = RecipeContainer.objects.get(id=model_id).recipe_original.count()
                return recipe
            except Exception as e:
                return -1
        elif classification == 'UF':
            return -1
        elif classification == 'CL':
            comment = Comment.objects.get(id=model_id).likes.count()
            return comment
        return -2

    def get_content(self, obj):
        classification = obj.classify
        model_id = obj.model_id
        if classification == 'RC':
            try:
                recipe = RecipeContainer.objects.get(id=model_id)
                comment = recipe.comment_container.all().order_by('-id').first()
                return comment.text
            except Exception as e:
                print(e)
        elif classification == 'CL':
            comment = Comment.objects.get(id=model_id)
            return comment.text
        else:
            return None
        return

    class Meta:
        model = History
        fields = ('id', 'object', 'classify', 'count', 'content', 'user', 'update_time')


# class RecipeContainerSerializer(serializers.ModelSerializer):       # RecipeDetail 용
#     recipe_order = RecipeSerializer(many=True)
#     multi_image_result = MultiImageResultSerializer(many=True)
#     extra_tip = ExtraTipSerializer(many=True)
#     comment_container = CommentSerializer(many=True)
#     ingredients = IngredientsSerializer(many=True)
#     likes = serializers.SerializerMethodField()
#     user = UserSerializer()
#
#     class Meta:
#         model = RecipeContainer
#         fields = '__all__'
#         depth = 1

#
# class RecipeContainerForHistorySerializer(serializers.ModelSerializer):     # 4번탭용 (History
#     multi_image_result = serializers.SerializerMethodField()
#     likes = RecipeLikeRelationSerializer(many=True)
#     likes_count = serializers.SerializerMethodField()
#
#     def get_multi_image_result(self, obj):
#         rep_image = MultiImageResult.objects.filter(recipe_container_id=obj.id).order_by('id')[:1]
#         print(rep_image)
#
#         for img in rep_image:
#             return img.image
#         return None
#
#     def get_likes_count(self, obj):
#         return obj.likes.count()
#
#     class Meta:
#         model = RecipeContainer
#         # fields = '__all__'
#         fields = ('id', 'recipe_title', 'food_name', 'multi_image_result', 'likes', 'likes_count')


class MyPageRecipeContainerSerializer(serializers.ModelSerializer):     # 2번탭용 Search
    multi_image_result = serializers.SerializerMethodField()
    image_count = serializers.SerializerMethodField()

    def get_image_count(self, obj):
        return obj.multi_image_result.count()

    def get_multi_image_result(self, obj):
        try:
            rep_image = obj.multi_image_result.all().order_by('-id').first().image.url
            return rep_image
        except:
            return None

    class Meta:
        model = RecipeContainer
        fields = ('id', 'multi_image_result', 'image_count', 'food_name')


class SimpleRecipeContainerSerializer(serializers.ModelSerializer):     # 2번탭용 Search
    multi_image_result = serializers.SerializerMethodField()
    likes_count = serializers.SerializerMethodField()

    def get_multi_image_result(self, obj):
        try:
            rep_image = MultiImageResult.objects.filter(recipe_container_id=obj.id).order_by('id').first().image.url
            return rep_image
        except:
            return None

    def get_likes_count(self, obj):
        return obj.likes.count()

    class Meta:
        model = RecipeContainer
        fields = ('id', 'recipe_title', 'food_name', 'multi_image_result', 'likes_count')


class SearchReferenceSerializer(serializers.ModelSerializer):     # 3번탭용 SearchReference
    multi_image_result = serializers.SerializerMethodField()
    likes_count = serializers.SerializerMethodField()
    user = SearchVerySimpleUserSerializer()

    def get_multi_image_result(self, obj):
        rep_image = MultiImageResult.objects.filter(recipe_container_id=obj.id).order_by('id').first().image.url
        return rep_image
        for img in rep_image:
            return img.image.url
        return None

    def get_likes_count(self, obj):
        return obj.likes.count()

    def get_post_count(self, obj):
        return obj.recipe_original.count()

    class Meta:
        model = RecipeContainer
        fields = ('id', 'recipe_title', 'food_name', 'multi_image_result', 'user', 'likes_count')


class SearchRecipeSerializer(serializers.ModelSerializer):     # 3번탭용 SearchReference
    multi_image_result = serializers.SerializerMethodField()
    likes_count = serializers.SerializerMethodField()
    post_count = serializers.SerializerMethodField()

    def get_multi_image_result(self, obj):
        rep_image = MultiImageResult.objects.filter(recipe_container_id=obj.id).order_by('id').first().image.url
        return rep_image
        for img in rep_image:
            return img.image.url
        return None

    def get_likes_count(self, obj):
        return obj.likes.count()

    def get_post_count(self, obj):
        return obj.recipe_original.count()

    class Meta:
        model = RecipeContainer
        fields = ('id', 'recipe_title', 'food_name', 'multi_image_result', 'copied_count', 'likes_count')


class SearchStorySerializer(serializers.ModelSerializer):     # 3번탭용 SearchReference
    multi_image_result = serializers.SerializerMethodField()
    likes_count = serializers.SerializerMethodField()

    def get_multi_image_result(self, obj):
        rep_image = MultiImageResult.objects.filter(recipe_container_id=obj.id).order_by('id').first().image.url
        return rep_image
        for img in rep_image:
            return img.image.url
        return None

    def get_likes_count(self, obj):
        return obj.likes.count()

    class Meta:
        model = RecipeContainer
        fields = ('id', 'introduce_recipe', 'multi_image_result', 'hit_count', 'likes_count')


class CopiedItemsSerializer(serializers.ModelSerializer):     # 3번탭용 SearchReference
    multi_image_result = serializers.SerializerMethodField()
    user = SimpleUserSerializer()

    def get_multi_image_result(self, obj):
        rep_image = MultiImageResult.objects.filter(recipe_container_id=obj.id).order_by('id').first().image.url
        return rep_image
        for img in rep_image:
            return img.image.url
        return None

    def get_likes_count(self, obj):
        return obj.likes.count()

    class Meta:
        model = RecipeContainer
        fields = ('id', 'multi_image_result', 'introduce_recipe', 'created_date', 'user')


class WishListSerializer(serializers.ModelSerializer):     # MyPage에 WishList용
    id = serializers.SerializerMethodField()
    multi_image_result = serializers.SerializerMethodField()
    food_name = serializers.SerializerMethodField()
    image_count = serializers.SerializerMethodField()

    def get_id(self, obj):
        recipe = RecipeContainer.objects.get(id=obj.recipe.id)
        print(recipe)
        return recipe.id

    def get_image_count(self, obj):
        recipe = RecipeContainer.objects.get(id=obj.recipe.id)
        return recipe.multi_image_result.count()

    def get_multi_image_result(self, obj):
        rep_image = MultiImageResult.objects.filter(recipe_container_id=obj.recipe.id).order_by('id').first().image.url
        return rep_image
        for img in rep_image:
            return img.image.url
        return None
    def get_food_name(self, obj):
        recipe = RecipeContainer.objects.get(id=obj.recipe.id)
        return recipe.food_name

    class Meta:
        model = RecipeWishListRelation
        fields = ('id', 'multi_image_result', 'image_count', 'food_name')


class VerySimpleRecipeContainerSerializer(serializers.ModelSerializer):     # 2번탭용 Search
    multi_image_result = serializers.SerializerMethodField()

    def get_multi_image_result(self, obj):
        rep_image = MultiImageResult.objects.filter(recipe_container_id=obj.id).order_by('id').first().image.url
        return rep_image
        for img in rep_image:
            return img.image.url
        return None

    class Meta:
        model = RecipeContainer
        fields = ('id', 'recipe_title', 'multi_image_result')


class HistoryForReferenceRecipeContainerSerializer(serializers.ModelSerializer):     # history용 모델
    multi_image_result = serializers.SerializerMethodField()
    # following_model = serializers.SerializerMethodField()
    #
    # def get_following_model(self, obj):
    #     copied = obj.recipe_original.all().first()
    #     try:
    #         copied = VerySimpleRecipeContainerSerializer(copied).data
    #         return copied
    #     except Exception as e:
    #         print("dkfjdskfj" , e)

    def get_multi_image_result(self, obj):
        recipe = RecipeContainer.objects.get(id=obj.id)
        rep_image = MultiImageResult.objects.filter(recipe_container_id=obj.id).order_by('id').first().image.url
        print(rep_image)
        return rep_image
        # try:
        # except Exception as e:
        #     print("----", e)
        #     return None

    class Meta:
        model = RecipeContainer
        fields = ('id', 'recipe_title', 'multi_image_result')


# class CommentForHistorySerializer(serializers.ModelSerializer):
#     recipe_container = VerySimpleRecipeContainerSerializer()
#     likes = CommentLikeRelationSerializer(many=True)
#
#     class Meta:
#         model = Comment
#         fields = ('id', 'created_date', 'likes', 'recipe_container')


# class HistoryUserSerializer(serializers.ModelSerializer):
#     relation_following = FollowRelationSerializer(many=True)
#
#     def get_followers(self, obj):
#         # f_list = FollowingRelation.objects.get(id=obj.id)
#         print(obj.id)
#         return True
#
#     class Meta:
#         model = User
#         fields = ('id', 'relation_following')
