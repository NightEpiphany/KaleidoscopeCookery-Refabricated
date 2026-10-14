package com.github.ysbbbbbb.kaleidoscopecookery.client.model;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.api.annotations.ClientThreadSafe;
import com.github.ysbbbbbb.kaleidoscopecookery.client.animation.TrashCanAnimation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.AnimationState;
import org.jspecify.annotations.NonNull;

@Environment(EnvType.CLIENT)
@SuppressWarnings({"unused","FieldCanBeLocal"})
public class TrashCanModel extends Model<TrashCanModel.State> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "trash_can"), "main");
    private final ModelPart root;
    private final ModelPart bone;
    private final ModelPart bone2;
    private final ModelPart eye;
    private final ModelPart bone3;
    private final KeyframeAnimation putAnimation;
    private final KeyframeAnimation withdrawAnimation;
    private final KeyframeAnimation playerAnimation1;
    private final KeyframeAnimation playerAnimation2;
    private final KeyframeAnimation enterAnimation;

    public TrashCanModel(ModelPart root) {
        super(root, RenderTypes::entityCutout);
        this.root = root.getChild("root");
        this.bone = this.root.getChild("bone");
        this.bone2 = this.root.getChild("bone2");
        this.eye = this.root.getChild("eye");
        this.bone3 = this.eye.getChild("bone3");

        this.putAnimation = TrashCanAnimation.PUT.bake(root);
        this.withdrawAnimation = TrashCanAnimation.WITHDRAW.bake(root);
        this.playerAnimation1 = TrashCanAnimation.PLAYER1.bake(root);
        this.playerAnimation2 = TrashCanAnimation.PLAYER2.bake(root);
        this.enterAnimation = TrashCanAnimation.ENTER.bake(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition bone = root.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(16, 40).addBox(-6.0F, -6.0F, -6.0F, 12.0F, 12.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -6.0F, 0.0F));

        PartDefinition bone2 = root.addOrReplaceChild("bone2", CubeListBuilder.create().texOffs(8, 0).addBox(-7.0F, -0.25F, -7.0F, 14.0F, 3.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -14.75F, 0.0F));

        PartDefinition cube_r1 = bone2.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(6, 26).addBox(-1.0F, -0.5F, -3.0F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(0, 20).addBox(-1.0F, -0.5F, -3.0F, 2.0F, 0.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(6, 26).addBox(-1.0F, -0.5F, 3.0F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.75F, 0.0F, 0.0F, -1.5708F, 0.0F));

        PartDefinition eye = root.addOrReplaceChild("eye", CubeListBuilder.create().texOffs(24, 22).addBox(-5.0F, -1.5F, -5.0F, 10.0F, 3.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -8.5F, 0.0F));

        PartDefinition bone3 = eye.addOrReplaceChild("bone3", CubeListBuilder.create().texOffs(34, 36).addBox(-5.0F, -3.0F, -5.0F, 10.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.5F, -0.1F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @ClientThreadSafe
    @Override
    public void setupAnim(@NonNull State object) {
        super.setupAnim(object);
        this.putAnimation.apply(object.putState, object.ageInTicks);
        this.withdrawAnimation.apply(object.withdrawState, object.ageInTicks);
        this.playerAnimation1.apply(object.player1State, object.ageInTicks);
        this.playerAnimation2.apply(object.player2State, object.ageInTicks);
        this.enterAnimation.apply(object.enterState, object.ageInTicks);
    }

    @Environment(EnvType.CLIENT)
    public record State(float ageInTicks, AnimationState putState, AnimationState withdrawState, AnimationState player1State, AnimationState player2State, AnimationState enterState) {
    }
}
