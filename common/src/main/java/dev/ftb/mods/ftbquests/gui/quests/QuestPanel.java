package dev.ftb.mods.ftbquests.gui.quests;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.icon.ImageIcon;
import dev.ftb.mods.ftblibrary.math.MathUtils;
import dev.ftb.mods.ftblibrary.ui.*;
import dev.ftb.mods.ftblibrary.ui.input.Key;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftblibrary.util.StringUtils;
import dev.ftb.mods.ftbquests.FTBQuests;
import dev.ftb.mods.ftbquests.net.CreateTaskAtMessage;
import dev.ftb.mods.ftbquests.net.EditObjectMessage;
import dev.ftb.mods.ftbquests.quest.ChapterImage;
import dev.ftb.mods.ftbquests.quest.Movable;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.QuestShape;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import dev.ftb.mods.ftbquests.quest.task.TaskTypes;
import dev.ftb.mods.ftbquests.quest.theme.property.ThemeProperties;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class QuestPanel extends Panel {
	private static final ImageIcon DEFAULT_DEPENDENCY_LINE_TEXTURE = (ImageIcon) Icon.getIcon(FTBQuests.MOD_ID + ":textures/gui/dependency.png");
	public final QuestScreen questScreen;
	public double questX = 0;
	public double questY = 0;
	public double centerQuestX = 0;
	public double centerQuestY = 0;
	public QuestButton mouseOverQuest = null;
	public double questMinX, questMinY, questMaxX, questMaxY;

	public QuestPanel(Panel panel) {
		super(panel);
		questScreen = (QuestScreen) panel.getGui();
	}

	public void updateMinMax() {
		questMinX = Double.POSITIVE_INFINITY;
		questMinY = Double.POSITIVE_INFINITY;
		questMaxX = Double.NEGATIVE_INFINITY;
		questMaxY = Double.NEGATIVE_INFINITY;

		for (Widget w : widgets) {
			double qx, qy, qw, qh;

			if (w instanceof QuestButton) {
				Quest q = ((QuestButton) w).quest;
				qx = q.x;
				qy = q.y;
				qw = q.size;
				qh = q.size;
			} else if (w instanceof ChapterImageButton) {
				ChapterImage q = ((ChapterImageButton) w).chapterImage;
				qx = q.x;
				qy = q.y;
				qw = q.width;
				qh = q.height;
			} else {
				continue;
			}

			questMinX = Math.min(questMinX, qx - qw / 2D);
			questMinY = Math.min(questMinY, qy - qh / 2D);
			questMaxX = Math.max(questMaxX, qx + qw / 2D);
			questMaxY = Math.max(questMaxY, qy + qh / 2D);
		}

		if (questMinX == Double.POSITIVE_INFINITY) {
			questMinX = questMinY = questMaxX = questMaxY = 0D;
		}

		questMinX -= 40D;
		questMinY -= 30D;
		questMaxX += 40D;
		questMaxY += 30D;
	}

	public void scrollTo(double x, double y) {
		updateMinMax();

		double dx = (questMaxX - questMinX);
		double dy = (questMaxY - questMinY);

		setScrollX((x - questMinX) / dx * questScreen.scrollWidth - width / 2D);
		setScrollY((y - questMinY) / dy * questScreen.scrollHeight - height / 2D);
	}

	public void resetScroll() {
		alignWidgets();
		setScrollX((questScreen.scrollWidth - width) / 2D);
		setScrollY((questScreen.scrollHeight - height) / 2D);
	}

	@Override
	public void addWidgets() {
		if (questScreen.selectedChapter == null) {
			return;
		}

		for (ChapterImage image : questScreen.selectedChapter.images) {
			if (questScreen.file.canEdit() || (!image.dev && (image.dependency == null || questScreen.file.self.isCompleted(image.dependency)))) {
				add(new ChapterImageButton(this, image));
			}
		}

		for (Quest quest : questScreen.selectedChapter.quests) {
			add(new QuestButton(this, quest));
		}

		alignWidgets();
	}

	@Override
	public void alignWidgets() {
		if (questScreen.selectedChapter == null) {
			return;
		}

		questScreen.scrollWidth = 0D;
		questScreen.scrollHeight = 0D;

		updateMinMax();

		double bs = questScreen.getQuestButtonSize();
		double bp = questScreen.getQuestButtonSpacing();

		questScreen.scrollWidth = (questMaxX - questMinX) * (bs + bp);
		questScreen.scrollHeight = (questMaxY - questMinY) * (bs + bp);

		for (Widget w : widgets) {
			double qx, qy, qw, qh;

			if (w instanceof QuestButton) {
				Quest q = ((QuestButton) w).quest;
				qx = q.x;
				qy = q.y;
				qw = q.size;
				qh = q.size;
			} else if (w instanceof ChapterImageButton) {
				ChapterImage q = ((ChapterImageButton) w).chapterImage;
				qx = q.x;
				qy = q.y;
				qw = q.width;
				qh = q.height;
			} else {
				continue;
			}

			double x = (qx - questMinX - qw / 2D) * (bs + bp) + bp / 2D + bp * (qw - 1D) / 2D;
			double y = (qy - questMinY - qh / 2D) * (bs + bp) + bp / 2D + bp * (qh - 1D) / 2D;
			w.setPosAndSize((int) x, (int) y, (int) (bs * qw), (int) (bs * qh));
		}

		setPosAndSize(20, 1, questScreen.width - 40, questScreen.height - 2);
	}

	@Override
	public void drawOffsetBackground(PoseStack matrixStack, Theme theme, int x, int y, int w, int h) {
		if (questScreen.selectedChapter == null) {
			return;
		}

		GuiHelper.setupDrawing();

		for (Widget widget : widgets) {
			if (widget instanceof ChapterImageButton) {
				widget.draw(matrixStack, theme, widget.getX(), widget.getY(), widget.width, widget.height);
			}
		}

		Tesselator tesselator = Tesselator.getInstance();
		BufferBuilder buffer = tesselator.getBuilder();

		Icon icon = ThemeProperties.DEPENDENCY_LINE_TEXTURE.get(questScreen.selectedChapter);
		if (icon instanceof ImageIcon img) {
			img.bindTexture();
		} else {
			DEFAULT_DEPENDENCY_LINE_TEXTURE.bindTexture();
		}

		Quest selectedQuest = questScreen.getViewedQuest();
		GuiHelper.setupDrawing();
		RenderSystem.enableDepthTest();
		double mt = -(System.currentTimeMillis() * 0.001D);
		float lineWidth = (float) (questScreen.getZoom() * ThemeProperties.DEPENDENCY_LINE_THICKNESS.get(questScreen.selectedChapter) / 4D * 3D);

		RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
		RenderSystem.setShaderColor(1F, 1F, 1F, 1F);

		// pass 1: render connections for all visible quests
		float mu = (float) ((mt * ThemeProperties.DEPENDENCY_LINE_UNSELECTED_SPEED.get(questScreen.selectedChapter)) % 1D);
		for (Widget widget : widgets) {
			if (widget.shouldDraw() && widget instanceof QuestButton qb && !qb.quest.getHideDependencyLines()) {
				boolean unavailable = questScreen.file.self == null || !questScreen.file.self.canStartTasks(qb.quest);
				boolean complete = !unavailable && questScreen.file.self != null && questScreen.file.self.isCompleted(qb.quest);

				for (QuestButton button : qb.getDependencies()) {
					if (button.quest != selectedQuest && qb.quest != selectedQuest) {
						int r, g, b, a;
						if (complete) {
							Color4I c = ThemeProperties.DEPENDENCY_LINE_COMPLETED_COLOR.get(questScreen.selectedChapter);
							r = c.redi();
							g = c.greeni();
							b = c.bluei();
							a = c.alphai();
						} else {
							Color4I c = Color4I.hsb(button.quest.id / 1000F, 0.2F, unavailable ? 0.3F : 0.8F);
							r = c.redi();
							g = c.greeni();
							b = c.bluei();
							a = 180;
						}
						renderConnection(widget, button, matrixStack, buffer, lineWidth, r, g, b, a, a, mu, tesselator);
					}
				}
			}

		}

		// pass 2: render highlighted connections for selected quest(s) dependencies/dependents
		float ms = (float) ((mt * ThemeProperties.DEPENDENCY_LINE_SELECTED_SPEED.get(questScreen.selectedChapter)) % 1D);
		List<QuestButton> toOutline = new ArrayList<>();
		for (Widget widget : widgets) {
			if (widget instanceof QuestButton qb && !qb.quest.getHideDependencyLines()) {
				for (QuestButton button : qb.getDependencies()) {
					int r, g, b, a, a2;
					if (button.quest == selectedQuest) {
						Color4I c = ThemeProperties.DEPENDENCY_LINE_REQUIRED_FOR_COLOR.get(questScreen.selectedChapter);
						r = c.redi();
						g = c.greeni();
						b = c.bluei();
						if (qb.shouldDraw()) {
							a = a2 = c.alphai();
						} else {
							a = c.alphai() / 4 * 3;
							a2 = 30;
							toOutline.add(qb);
						}
					} else if (qb.quest == selectedQuest) {
						Color4I c = ThemeProperties.DEPENDENCY_LINE_REQUIRES_COLOR.get(questScreen.selectedChapter);
						r = c.redi();
						g = c.greeni();
						b = c.bluei();
						a = c.alphai();
						a2 = a;
					} else {
						continue;
					}
					renderConnection(widget, button, matrixStack, buffer, lineWidth, r, g, b, a2, a, ms, tesselator);
				}
			}

		}
		toOutline.forEach(qb -> {
			QuestShape.get(qb.quest.getShape()).shape.withColor(Color4I.BLACK.withAlpha(30)).draw(matrixStack, qb.getX(), qb.getY(), qb.width, qb.height);
			QuestShape.get(qb.quest.getShape()).outline.withColor(Color4I.BLACK.withAlpha(90)).draw(matrixStack, qb.getX(), qb.getY(), qb.width, qb.height);
		});
	}

	private void renderConnection(Widget widget, QuestButton button, PoseStack matrixStack, BufferBuilder buffer, float s, int r, int g, int b, int a, int a1, float mu, Tesselator tesselator) {
		int sx = widget.getX() + widget.width / 2;
		int sy = widget.getY() + widget.height / 2;
		int ex = button.getX() + button.width / 2;
		int ey = button.getY() + button.height / 2;
		float len = (float) MathUtils.dist(sx, sy, ex, ey);

		matrixStack.pushPose();
		matrixStack.translate(sx, sy, 0);
		matrixStack.mulPose(Vector3f.ZP.rotation((float) Math.atan2(ey - sy, ex - sx)));
		Matrix4f m = matrixStack.last().pose();

		buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
		buffer.vertex(m, 0, -s, 0).color(r, g, b, a).uv(len / s / 2F + mu, 0).endVertex();
		buffer.vertex(m, 0, s, 0).color(r, g, b, a).uv(len / s / 2F + mu, 1).endVertex();
		buffer.vertex(m, len, s, 0).color(r * 3 / 4, g * 3 / 4, b * 3 / 4, a1).uv(mu, 1).endVertex();
		buffer.vertex(m, len, -s, 0).color(r * 3 / 4, g * 3 / 4, b * 3 / 4, a1).uv(mu, 0).endVertex();
		tesselator.end();

		matrixStack.popPose();
	}

	@Override
	public void draw(PoseStack matrixStack, Theme theme, int x, int y, int w, int h) {
		super.draw(matrixStack, theme, x, y, w, h);

		if (questScreen.selectedChapter != null && isMouseOver()) {
			//updateMinMax();

			double dx = (questMaxX - questMinX);
			double dy = (questMaxY - questMinY);

			double px = getX() - getScrollX();
			double py = getY() - getScrollY();

			double qx = (questScreen.getMouseX() - px) / questScreen.scrollWidth * dx + questMinX;
			double qy = (questScreen.getMouseY() - py) / questScreen.scrollHeight * dy + questMinY;
			centerQuestX = (questScreen.width / 2D - px) / questScreen.scrollWidth * dx + questMinX;
			centerQuestY = (questScreen.height / 2D - py) / questScreen.scrollHeight * dy + questMinY;

			if (isShiftKeyDown()) {
				questX = qx;
				questY = qy;
			} else if (questScreen.selectedObjects.size() == 1 && questScreen.selectedObjects.get(0) instanceof Quest q) {
				double s = (1D / questScreen.file.gridScale) / q.size;
				questX = Mth.floor(qx * s + 0.5D) / s;
				questY = Mth.floor(qy * s + 0.5D) / s;
			} else {
				double s = 1D / questScreen.file.gridScale;
				questX = Mth.floor(qx * s + 0.5D) / s;
				questY = Mth.floor(qy * s + 0.5D) / s;
			}

			if (questScreen.file.canEdit()) {
				matrixStack.pushPose();
				matrixStack.translate(0, 0, 600);
				theme.drawString(matrixStack, "X:" + (questX < 0 ? "" : " ") + StringUtils.DOUBLE_FORMATTER_00.format(questX), x + 3, y + h - 18, Theme.SHADOW);
				theme.drawString(matrixStack, "Y:" + (questY < 0 ? "" : " ") + StringUtils.DOUBLE_FORMATTER_00.format(questY), x + 3, y + h - 10, Theme.SHADOW);

				if (!questScreen.selectedObjects.isEmpty()) {
					theme.drawString(matrixStack, "Selected: " + questScreen.selectedObjects.size(), x + 3, y + h - 26, Theme.SHADOW);
				}

				theme.drawString(matrixStack, "CX:" + (centerQuestX < 0 ? "" : " ") + StringUtils.DOUBLE_FORMATTER_00.format(centerQuestX), x + w - 42, y + h - 18, Theme.SHADOW);
				theme.drawString(matrixStack, "CY:" + (centerQuestY < 0 ? "" : " ") + StringUtils.DOUBLE_FORMATTER_00.format(centerQuestY), x + w - 42, y + h - 10, Theme.SHADOW);
				matrixStack.popPose();

				double bs = questScreen.getQuestButtonSize();

				if (questScreen.movingObjects && !questScreen.selectedObjects.isEmpty()) {
					double ominX = Double.POSITIVE_INFINITY, ominY = Double.POSITIVE_INFINITY, omaxX = Double.NEGATIVE_INFINITY, omaxY = Double.NEGATIVE_INFINITY;

					for (Movable q : questScreen.selectedObjects) {
						ominX = Math.min(ominX, q.getX());
						ominY = Math.min(ominY, q.getY());
						omaxX = Math.max(omaxX, q.getX());
						omaxY = Math.max(omaxY, q.getY());
					}

					for (Movable m : questScreen.selectedObjects) {
						double ox = m.getX() - ominX;
						double oy = m.getY() - ominY;
						double sx = (questX + ox - questMinX) / dx * questScreen.scrollWidth + px;
						double sy = (questY + oy - questMinY) / dy * questScreen.scrollHeight + py;
						matrixStack.pushPose();
						matrixStack.translate(sx - bs * m.getWidth() / 2D, sy - bs * m.getHeight() / 2D, 0D);
						matrixStack.scale((float) (bs * m.getWidth()), (float) (bs * m.getHeight()), 1F);
						GuiHelper.setupDrawing();
						RenderSystem.enableDepthTest();
						m.drawMoved(matrixStack);
						matrixStack.popPose();
					}

					if (QuestScreen.grid && questScreen.viewQuestPanel.quest == null) {
						double boxX = ominX / dx * questScreen.scrollWidth + px;
						double boxY = ominY / dy * questScreen.scrollHeight + py;
						double boxW = omaxX / dx * questScreen.scrollWidth + px - boxX;
						double boxH = omaxY / dy * questScreen.scrollHeight + py - boxY;

						matrixStack.pushPose();
						matrixStack.translate(0, 0, 1000);
						GuiHelper.drawHollowRect(matrixStack, (int) boxX, (int) boxY, (int) boxW, (int) boxH, Color4I.WHITE.withAlpha(30), false);
						matrixStack.popPose();
					}
				} else if (questScreen.viewQuestPanel.quest == null || !questScreen.viewQuestPanel.isMouseOver()) {
					//int z = treeGui.getZoom();
					double sx = (questX - questMinX) / dx * questScreen.scrollWidth + px;
					double sy = (questY - questMinY) / dy * questScreen.scrollHeight + py;
					matrixStack.pushPose();
					matrixStack.translate(sx - bs / 2D, sy - bs / 2D, 0D);
					matrixStack.scale((float) bs, (float) bs, 1F);
					GuiHelper.setupDrawing();
					RenderSystem.enableDepthTest();
					// TODO: custom shader to implement alphaFunc? for now however, rendering outline at alpha 30 works well
					//RenderSystem.alphaFunc(GL11.GL_GREATER, 0.01F);
					QuestShape.get(questScreen.selectedChapter.getDefaultQuestShape()).outline.withColor(Color4I.WHITE.withAlpha(30)).draw(matrixStack, 0, 0, 1, 1);
					//RenderSystem.defaultAlphaFunc();
					matrixStack.popPose();

					if (QuestScreen.grid && questScreen.viewQuestPanel.quest == null) {
						matrixStack.pushPose();
						matrixStack.translate(0, 0, 1000);
						Color4I.WHITE.draw(matrixStack, (int) sx, (int) sy, 1, 1);
						Color4I.WHITE.withAlpha(30).draw(matrixStack, getX(), (int) sy, width, 1);
						Color4I.WHITE.withAlpha(30).draw(matrixStack, (int) sx, getY(), 1, height);
						matrixStack.popPose();
					}
				}
			}
		}
	}

	@Override
	public boolean mousePressed(MouseButton button) {
		if (questScreen.selectedChapter == null || questScreen.chapterPanel.isMouseOver()) {
			return false;
		}

		if (questScreen.movingObjects && questScreen.file.canEdit()) {
			if (questScreen.selectedChapter != null && !button.isRight() && !questScreen.selectedObjects.isEmpty()) {
				playClickSound();

				double minX = Double.POSITIVE_INFINITY;
				double minY = Double.POSITIVE_INFINITY;

				for (Movable q : questScreen.selectedObjects) {
					minX = Math.min(minX, q.getX());
					minY = Math.min(minY, q.getY());
				}

				for (Movable q : new ArrayList<>(questScreen.selectedObjects)) {
					q.move(questScreen.selectedChapter, questX + (q.getX() - minX), questY + (q.getY() - minY));
				}
			}

			questScreen.movingObjects = false;
			questScreen.selectedObjects.clear();
			return true;
		}

		if (super.mousePressed(button)) {
			return true;
		}

		if (!questScreen.viewQuestPanel.hidePanel && questScreen.isViewingQuest()) {
			questScreen.closeQuest();
			return true;
		}

		if ((button.isLeft() || button.isMiddle() && questScreen.file.canEdit()) && isMouseOver() && (questScreen.viewQuestPanel.hidePanel || !questScreen.isViewingQuest())) {
			questScreen.prevMouseX = getMouseX();
			questScreen.prevMouseY = getMouseY();
			questScreen.grabbed = button;
			return true;
		}

		if (button.isRight() && questScreen.file.canEdit()) {
			playClickSound();
			List<ContextMenuItem> contextMenu = new ArrayList<>();
			double qx = questX;
			double qy = questY;

			for (TaskType type : TaskTypes.TYPES.values()) {
				contextMenu.add(new ContextMenuItem(type.getDisplayName(), type.getIcon(), () -> {
					playClickSound();
					type.getGuiProvider().openCreationGui(this, new Quest(questScreen.selectedChapter), task -> new CreateTaskAtMessage(questScreen.selectedChapter, qx, qy, task).sendToServer());
				}));
			}

			contextMenu.add(new ContextMenuItem(new TranslatableComponent("ftbquests.chapter.image"), Icons.ART, () -> {
				playClickSound();
				ChapterImage image = new ChapterImage(questScreen.selectedChapter);
				image.x = qx;
				image.y = qy;
				questScreen.selectedChapter.images.add(image);
				new EditObjectMessage(questScreen.selectedChapter).sendToServer();
			}));

			questScreen.openContextMenu(contextMenu);
			return true;
		}

		return false;
	}

	@Override
	public void mouseReleased(MouseButton button) {
		super.mouseReleased(button);

		if (questScreen.grabbed != null && questScreen.grabbed.isMiddle() && questScreen.file.canEdit()) {
			// select any quests in the box
			questScreen.selectAllQuestsInBox(getMouseX(), getMouseY(), getScrollX(), getScrollY());
		}

		questScreen.grabbed = null;
	}

	@Override
	public boolean checkMouseOver(int mouseX, int mouseY) {
		if (questScreen.chapterPanel.isMouseOver()) {
			return false;
		}

		return super.checkMouseOver(mouseX, mouseY);
	}

	@Override
	public void updateMouseOver(int mouseX, int mouseY) {
		mouseOverQuest = null;
		super.updateMouseOver(mouseX, mouseY);

		for (Widget widget : widgets) {
			if (widget.isMouseOver() && widget instanceof QuestButton) {
				mouseOverQuest = (QuestButton) widget;
				break;
			}
		}
	}

	@Override
	public boolean keyPressed(Key key) {
		if (questScreen.selectedChapter != null && !questScreen.isViewingQuest() && (key.is(GLFW.GLFW_KEY_MINUS) || key.is(GLFW.GLFW_KEY_EQUAL))) {
			questScreen.addZoom(key.is(GLFW.GLFW_KEY_MINUS) ? -1D : 1D);
			return true;
		}

		return super.keyPressed(key);
	}

	@Override
	public boolean scrollPanel(double scroll) {
		if (questScreen.selectedChapter != null && !questScreen.isViewingQuest() && isMouseOver()) {
			questScreen.addZoom(scroll);
			return true;
		}

		return false;
	}
}