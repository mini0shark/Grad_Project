import django_filters
from recipeApi.models import RecipeContainer


class RecipeContainerFilter(django_filters.FilterSet):

    class Meta:
        model = RecipeContainer
        fields = ['user', ' type']