from django.contrib import admin
from django.urls import path, include
from drf_yasg.views import get_schema_view
from drf_yasg import openapi
from rest_framework.permissions import AllowAny, IsAuthenticated, BasePermission
from django.conf.urls.static import static
from django.conf import settings

schema_url_patters = [
    path('userApi/', include('userApi.urls')),
    path('recipeApi/', include('recipeApi.urls')),
    path('chatting/', include('chatting.urls')),
]

schema_view = get_schema_view(
    openapi.Info(
        title="Rest Open Api",
        default_version='v1',
        description="""
        하하호호 문서입니다. 하하호호 모임""",
        terms_of_service="http://127.0.0.1:8000/recipeApi/boards/",
        contact=openapi.Contact(email="mmi@naver.com"),
        license=openapi.License(name='하하하하....'),
    ),
    validators=['flex'],
    public=True,
    permission_classes=(AllowAny, BasePermission,),
    patterns=schema_url_patters,
)

urlpatterns = [
    path('admin/', admin.site.urls),  ## 기본으로 제공되는 관리 페이지
    path('chatting/', include('chatting.urls')),
    path('userApi/', include('userApi.urls')),  ## user에 있는 urls
    path('recipeApi/', include('recipeApi.urls')),
    path('swagger<str:format>', schema_view.without_ui(cache_timeout=0), name='schema-json'),
    path('swagger/', schema_view.with_ui('swagger', cache_timeout=0), name='schema-swagger-ui'),
    path('docs/', schema_view.with_ui('redoc', cache_timeout=0), name='schema-redoc'),
    # path('accounts/', include('allauth.urls')),
    # path('', login_required(kilogram_views.IndexView.as_view()), name='root'),
]
urlpatterns += static(settings.MEDIA_URL, document_root=settings.MEDIA_ROOT)

# Django가 함수 include()를 만나게 되면, URL의 그 시점까지 일치하는 부분을 잘라내고,
# # # 남은 문자열 부분을 후속 처리를 위해 include 된 URLconf로 전달합니다.
## user를 예로 들면 userApi/ 이후에 있는 내용은 userApi.userApi 에서 처리 하겠다는 뜻(뒤에 주소가 있을 수 있음
"""RecipeServer URL Configuration

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/2.2/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  path('', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))
"""
