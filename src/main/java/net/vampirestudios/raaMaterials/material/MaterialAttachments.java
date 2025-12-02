package net.vampirestudios.raaMaterials.material;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.vampirestudios.raaMaterials.RAAMaterials;

public final class MaterialAttachments {
	public static final AttachmentType<MaterialSet> MATERIALS = AttachmentRegistry.create(
			RAAMaterials.id("materials"),
			(AttachmentRegistry.Builder<MaterialSet> b) -> b
					.initializer(() -> MaterialSet.DEFAULT)
					.persistent(MaterialCodecs.MATERIAL_SET)
					.syncWith(MaterialSet.PACKET_CODEC, AttachmentSyncPredicate.all())
	);
	public static final AttachmentType<LegendaryAssignments> LEGENDARIES = AttachmentRegistry.create(
			RAAMaterials.id("legendaries"),
			(AttachmentRegistry.Builder<LegendaryAssignments> b) -> b
					.initializer(LegendaryAssignments::empty)
					.persistent(LegendaryAssignments.CODEC)
					.syncWith(LegendaryAssignments.PACKET_CODEC, AttachmentSyncPredicate.all())
	);

	private MaterialAttachments() {
	}
}
