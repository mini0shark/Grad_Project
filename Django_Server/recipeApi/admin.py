from django.contrib import admin
from recipeApi.models import RecipeContainer, MultiImageResult, Recipe


admin.site.register(MultiImageResult)
admin.site.register(Recipe)
admin.site.register(RecipeContainer)
