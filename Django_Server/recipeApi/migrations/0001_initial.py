# Generated by Django 3.0.3 on 2020-04-08 07:22

from django.db import migrations, models
import django.db.models.deletion
import recipeApi.models


class Migration(migrations.Migration):

    initial = True

    dependencies = [
        ('userApi', '0001_initial'),
    ]

    operations = [
        migrations.CreateModel(
            name='Comment',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('created_date', models.DateTimeField(auto_now_add=True, null=True)),
                ('text', models.TextField(blank=True, null=True)),
            ],
        ),
        migrations.CreateModel(
            name='RecipeContainer',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('recipe_title', models.CharField(blank=True, max_length=200, null=True)),
                ('food_name', models.CharField(blank=True, max_length=200, null=True)),
                ('introduce_recipe', models.TextField(blank=True, null=True)),
                ('category', models.CharField(blank=True, max_length=50, null=True)),
                ('for_person', models.IntegerField(blank=True, default=0, null=True)),
                ('required_time', models.IntegerField(blank=True, default=0, null=True)),
                ('created_date', models.DateTimeField(auto_now_add=True, null=True)),
                ('type', models.BooleanField(default=False)),
                ('hit_count', models.PositiveIntegerField(blank=True, default=0)),
            ],
        ),
        migrations.CreateModel(
            name='ReferencesRelation',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('created_time', models.DateTimeField(auto_now_add=True, null=True)),
                ('recipe_copied', models.ForeignKey(blank=True, null=True, on_delete=django.db.models.deletion.CASCADE, related_name='recipe_copied', to='recipeApi.RecipeContainer')),
                ('recipe_original', models.ForeignKey(blank=True, null=True, on_delete=django.db.models.deletion.CASCADE, related_name='recipe_original', to='recipeApi.RecipeContainer')),
            ],
        ),
        migrations.CreateModel(
            name='RecipeWishListRelation',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('created_time', models.DateTimeField(auto_now_add=True, null=True)),
                ('recipe', models.ForeignKey(blank=True, null=True, on_delete=django.db.models.deletion.CASCADE, related_name='recipe_wish_list_recipe', to='recipeApi.RecipeContainer')),
                ('user', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, related_name='recipe_wish_list_user', to='userApi.User')),
            ],
        ),
        migrations.CreateModel(
            name='RecipeLikeRelation',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('created_time', models.DateTimeField(auto_now_add=True, null=True)),
                ('recipe', models.ForeignKey(blank=True, null=True, on_delete=django.db.models.deletion.CASCADE, related_name='recipe_like_recipe', to='recipeApi.RecipeContainer')),
                ('user', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, related_name='recipe_like_user', to='userApi.User')),
            ],
        ),
        migrations.AddField(
            model_name='recipecontainer',
            name='likes',
            field=models.ManyToManyField(related_name='likes_for_container', through='recipeApi.RecipeLikeRelation', to='userApi.User'),
        ),
        migrations.AddField(
            model_name='recipecontainer',
            name='list',
            field=models.ManyToManyField(related_name='list_for_container', through='recipeApi.RecipeWishListRelation', to='userApi.User'),
        ),
        migrations.AddField(
            model_name='recipecontainer',
            name='reference',
            field=models.ManyToManyField(related_name='references_recipe_container', through='recipeApi.ReferencesRelation', to='recipeApi.RecipeContainer'),
        ),
        migrations.AddField(
            model_name='recipecontainer',
            name='user',
            field=models.ForeignKey(blank=True, on_delete=django.db.models.deletion.DO_NOTHING, related_name='recipe_container', to='userApi.User'),
        ),
        migrations.CreateModel(
            name='Recipe',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('order', models.IntegerField(default=0, null=True)),
                ('image', models.ImageField(blank=True, null=True, upload_to=recipeApi.models.recipe_order_path)),
                ('explain', models.TextField(blank=True, null=True)),
                ('recipe_container', models.ForeignKey(blank=True, on_delete=django.db.models.deletion.CASCADE, related_name='recipe_order', to='recipeApi.RecipeContainer')),
            ],
        ),
        migrations.CreateModel(
            name='MultiImageResult',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('image', models.ImageField(null=True, upload_to=recipeApi.models.multiple_result_images_path)),
                ('recipe_container', models.ForeignKey(blank=True, on_delete=django.db.models.deletion.CASCADE, related_name='multi_image_result', to='recipeApi.RecipeContainer')),
            ],
        ),
        migrations.CreateModel(
            name='Ingredients',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('ingredient', models.CharField(max_length=100, null=True)),
                ('amount', models.CharField(max_length=100, null=True)),
                ('recipe_container', models.ForeignKey(blank=True, on_delete=django.db.models.deletion.CASCADE, related_name='ingredients', to='recipeApi.RecipeContainer')),
            ],
        ),
        migrations.CreateModel(
            name='ExtraTip',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('tip', models.TextField()),
                ('recipe_container', models.ForeignKey(blank=True, on_delete=django.db.models.deletion.CASCADE, related_name='extra_tip', to='recipeApi.RecipeContainer')),
            ],
        ),
        migrations.CreateModel(
            name='CommentLikeRelation',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('created_time', models.DateTimeField(auto_now_add=True, null=True)),
                ('from_user', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, related_name='comment_like_user', to='userApi.User')),
                ('to_comment', models.ForeignKey(blank=True, null=True, on_delete=django.db.models.deletion.CASCADE, related_name='comment_like_comment', to='recipeApi.Comment')),
            ],
        ),
        migrations.AddField(
            model_name='comment',
            name='likes',
            field=models.ManyToManyField(related_name='likes_for_comment', through='recipeApi.CommentLikeRelation', to='userApi.User'),
        ),
        migrations.AddField(
            model_name='comment',
            name='recipe_container',
            field=models.ForeignKey(blank=True, null=True, on_delete=django.db.models.deletion.CASCADE, related_name='comment_container', to='recipeApi.RecipeContainer'),
        ),
        migrations.AddField(
            model_name='comment',
            name='tag',
            field=models.ManyToManyField(related_name='comment_tag', to='userApi.User'),
        ),
        migrations.AddField(
            model_name='comment',
            name='user',
            field=models.ForeignKey(blank=True, null=True, on_delete=django.db.models.deletion.CASCADE, related_name='comment_user', to='userApi.User'),
        ),
        migrations.CreateModel(
            name='History',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('update_time', models.DateTimeField(auto_now_add=True)),
                ('model_id', models.IntegerField(default=0, null=True)),
                ('classify', models.CharField(choices=[('RL', 'Recipe Like'), ('RC', 'Recipe Comment'), ('RR', 'Recipe Reference'), ('UF', 'User Follow'), ('CL', 'Comment Like')], max_length=2)),
                ('user', models.ForeignKey(null=True, on_delete=django.db.models.deletion.CASCADE, related_name='history_user', to='userApi.User')),
            ],
            options={
                'unique_together': {('classify', 'model_id')},
            },
        ),
    ]
