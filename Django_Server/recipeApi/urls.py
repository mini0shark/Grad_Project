from django.urls import path
from recipeApi import views


urlpatterns = [
    # ex /userApi/
    # ex /userApi/
    path('<user_id>/recipeContainer', views.register_recipe_container, name='recipeContainer'),
    path('recipeContainer/<recipe_id>', views.edit_recipe_container, name='recipeContainer'),
    path('<user_id>/story', views.register_story, name='story'),

    path("Recipe/<id>", views.ContainerDetailAPIView.as_view(), name="container"),
    path("recipeDetail/<recipe_id>", views.RecipeDetailAPIView.as_view(), name="recipeDetail"),
    path("storyDetail/<recipe_id>", views.StoryDetailAPIView.as_view(), name="storyDetail"),
    path("recipeList/<user_id>", views.HomeContainerList.as_view(), name="recipeList"),

    path("registerRecipeComment", views.CommentList.as_view(), name="registerRecipeComment"),
    path("like/<user_id>/<recipe_id>", views.push_like, name="pushLikeButton"),
    path("commentLike/<user_id>/<comment_id>", views.push_comment_like, name="pushCommentLikeButton"),
    path("Comment/<comment_id>", views.CommentDetailAPIView.as_view(), name="Comment"),
    path("WishList", views.WishListList.as_view(), name="addToWishList"),

    path('searchInit/<user_id>', views.SearchContainerList.as_view(), name='searchInit'),
    path('searchRecipe', views.SearchRecipeList.as_view(), name='searchRecipe'),
    path('searchStory', views.SearchStoryList.as_view(), name='searchStory'),

    path('searchReference', views.SearchReferenceList.as_view(), name='searchReference'),
    path('searchReferenceMyRecipes/<user_id>', views.MyReferenceList.as_view(), name='searchReferenceMyRecipes'),

    path('history/<user_id>', views.get_user_history, name='history'),
    path('history/copiedList/<recipe_id>', views.CopiedStoryList.as_view(), name='copiedList'),
    path('myPage/items/<user_id>/<kind>', views.get_my_page_item, name='myPage_Items'),
]
