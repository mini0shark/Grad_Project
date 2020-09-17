from django.db import models
from userApi.models import User


def recipe_main_image_path(instance, filename):
    # file will be uploaded to MEDIA_ROOT/user_<id>/<filename>
    return 'user_{0}/recipe/mainImages/{1}'.format(instance.user.id, filename)


class RecipeContainer(models.Model):
    user = models.ForeignKey(User, on_delete=models.DO_NOTHING, related_name='recipe_container', blank=True)

    recipe_title = models.CharField(max_length=200, null=True, blank=True)
    food_name = models.CharField(max_length=200, null=True, blank=True)
    introduce_recipe = models.TextField(null=True, blank=True)
    category = models.CharField(max_length=50, null=True, blank=True)
    for_person = models.IntegerField(default=0, null=True, blank=True)
    required_time = models.IntegerField(default=0, null=True, blank=True)
    created_date = models.DateTimeField(auto_now_add=True, null=True, blank=True)
    type = models.BooleanField(default=False)  # False => 스토리 True => Recipe
    reference = models.ManyToManyField("self", related_name='references_recipe_container', symmetrical=False, through='ReferencesRelation')

    likes = models.ManyToManyField(User, related_name='likes_for_container', through='RecipeLikeRelation')
    list = models.ManyToManyField(User, related_name='list_for_container', through='RecipeWishListRelation')
    hit_count = models.PositiveIntegerField(default=0, blank=True)

    def total_likes(self):
        return self.likes.count()


def recipe_order_path(instance, filename):
    # file will be uploaded to MEDIA_ROOT/user_<id>/<filename>
    return 'user_{0}/recipe/recipe/{1}'.format(instance.recipe_container.user.id, filename)


class Recipe(models.Model):
    recipe_container = models.ForeignKey(RecipeContainer,
                                         on_delete=models.CASCADE,
                                         related_name='recipe_order',
                                         blank=True)
    order = models.IntegerField(default=0, null=True)
    image = models.ImageField(upload_to=recipe_order_path, null=True, blank=True)
    explain = models.TextField(null=True, blank=True)

    def __str__(self):
        return "Recipe" + str(self.pk) + " : " + self.user + " : " + str(self.user)


def multiple_result_images_path(instance, filename):
    # file will be uploaded to MEDIA_ROOT/user_<id>/<filename>
    middle_path = filename.split("_")[6]
    return 'user_{0}/recipe/resultImage/{1}/{2}'.format(instance.recipe_container.user.id, middle_path, filename)


class MultiImageResult(models.Model):  # 결과 이미지
    recipe_container = models.ForeignKey(RecipeContainer, on_delete=models.CASCADE, related_name='multi_image_result',
                                         blank=True)
    image = models.ImageField(upload_to=multiple_result_images_path, null=True)


class Comment(models.Model):
    recipe_container = models.ForeignKey(RecipeContainer, on_delete=models.CASCADE, related_name='comment_container',
                                         blank=True, null=True)
    user = models.ForeignKey(User, on_delete=models.CASCADE, related_name='comment_user', blank=True, null=True)
    created_date = models.DateTimeField(auto_now_add=True, null=True, blank=True)
    text = models.TextField(null=True, blank=True)
    tag = models.ManyToManyField(User, related_name='comment_tag')
    likes = models.ManyToManyField(User, related_name='likes_for_comment', symmetrical=True,
                                   through='CommentLikeRelation')

    def total_likes(self):
        return self.likes.count()


class Ingredients(models.Model):
    recipe_container = models.ForeignKey(RecipeContainer, on_delete=models.CASCADE, related_name='ingredients',
                                         blank=True)
    ingredient = models.CharField(max_length=100, null=True)
    amount = models.CharField(max_length=100, null=True)


class ExtraTip(models.Model):
    recipe_container = models.ForeignKey(RecipeContainer, on_delete=models.CASCADE, related_name='extra_tip',
                                         blank=True)
    tip = models.TextField()


# #################################################### 중계모델


class ReferencesRelation(models.Model):
    recipe_original = models.ForeignKey(RecipeContainer, on_delete=models.CASCADE, related_name="recipe_original",
                                        null=True, blank=True)
    recipe_copied = models.ForeignKey(RecipeContainer, on_delete=models.CASCADE, related_name="recipe_copied",
                                      null=True, blank=True)
    created_time = models.DateTimeField(auto_now_add=True, null=True, blank=True)


class RecipeWishListRelation(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE, related_name="recipe_wish_list_user")
    recipe = models.ForeignKey(RecipeContainer, on_delete=models.CASCADE, related_name="recipe_wish_list_recipe",
                               null=True, blank=True)
    created_time = models.DateTimeField(auto_now_add=True, null=True, blank=True)


class RecipeLikeRelation(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE, related_name="recipe_like_user")
    recipe = models.ForeignKey(RecipeContainer, on_delete=models.CASCADE, related_name="recipe_like_recipe",
                               null=True, blank=True)
    created_time = models.DateTimeField(auto_now_add=True, null=True, blank=True)


class CommentLikeRelation(models.Model):
    to_comment = models.ForeignKey(Comment, on_delete=models.CASCADE, related_name="comment_like_comment", null=True,
                                   blank=True)
    from_user = models.ForeignKey(User, on_delete=models.CASCADE, related_name="comment_like_user")
    created_time = models.DateTimeField(auto_now_add=True, null=True, blank=True)


class History(models.Model):
    class Meta:
        unique_together = ('classify', 'model_id', 'user_id')

    classification = (
        ('RL', 'Recipe Like'),
        ('RC', 'Recipe Comment'),
        ('RR', 'Recipe Reference'),
        ('UF', 'User Follow'),
        ('CL', 'Comment Like'),
    )
    user = models.ForeignKey(User, on_delete=models.CASCADE, related_name="history_user", null=True)
    update_time = models.DateTimeField(auto_now_add=True, blank=True)
    model_id = models.IntegerField(default=0, null=True)
    classify = models.CharField(max_length=2, choices=classification)
